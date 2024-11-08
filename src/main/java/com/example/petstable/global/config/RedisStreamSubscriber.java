package com.example.petstable.global.config;

import com.example.petstable.domain.point.event.PointEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamSubscriber {
    private final PointEventListener pointEventListener;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConnectionFactory redisConnectionFactory;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> pointListenerContainer;

    @PostConstruct
    public void createConsumer() {
        createStreamConsumerGroup("recipePoint", "coursePointGroup");
    }

    public void createStreamConsumerGroup(String streamKey, final String consumerGroupName) {
        boolean streamExists = Boolean.TRUE.equals(redisTemplate.hasKey(streamKey));
        if (!streamExists) {
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                byte[] streamKeyBytes = streamKey.getBytes();
                byte[] consumerGroupNameBytes = consumerGroupName.getBytes();
                connection.execute("XGROUP", "CREATE".getBytes(), streamKeyBytes, consumerGroupNameBytes,
                        "0".getBytes(), "MKSTREAM".getBytes());
                return null;
            });
        } else if (!isStreamConsumerGroupExist(streamKey, consumerGroupName)) {
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.from("0"), consumerGroupName);
        }
    }

    public boolean isStreamConsumerGroupExist(final String streamKey, final String consumerGroupName) {
        return redisTemplate
                .opsForStream().groups(streamKey).stream()
                .anyMatch(group -> group.groupName().equals(consumerGroupName));
    }

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> startPointListener() {
        pointListenerContainer = createStreamSubscription(
                "recipePoint", "recipePointGroup", "instance-1", pointEventListener
        );
        return pointListenerContainer;
    }

    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> createStreamSubscription(
            String streamKey, String consumerGroup, String consumerName,
            StreamListener<String, MapRecord<String, String, String>> eventListener) {

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1L))
                .errorHandler(e -> {
                    log.error("Error in listener: {}", e.getMessage());
                    restartSubscription(streamKey, consumerGroup, consumerName, eventListener);
                }).build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions);

        container.register(
                StreamMessageListenerContainer.StreamReadRequest.builder(
                                StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
                        .cancelOnError(t -> true) // 오류 발생 시 구독 취소
                        .consumer(Consumer.from(consumerGroup, consumerName))
                        .autoAcknowledge(false)
                        .build(), eventListener);

        container.start();
        log.info("Listener container started for stream: {}", streamKey);
        return container;
    }

    private void restartSubscription(String streamKey, String consumerGroup, String consumerName,
                                     StreamListener<String, MapRecord<String, String, String>> eventListener) {
        scheduler.schedule(() -> {
            log.info("Restarting subscription for stream: {}", streamKey);
            stopContainer(streamKey);
            createStreamSubscription(streamKey, consumerGroup, consumerName, eventListener).start();
        }, 5, TimeUnit.SECONDS); // 일정 시간 후 재시작
    }

    private void stopContainer(String streamKey) {
        if ("recipePoint".equals(streamKey) && pointListenerContainer != null && pointListenerContainer.isRunning()) {
            pointListenerContainer.stop();
            log.info("Stopped point listener container");
        }
    }

    @PreDestroy
    public void onDestroy() {
        stopContainer("recipePoint");
        scheduler.shutdown();
        log.info("All listener containers stopped and scheduler shutdown.");
    }
}