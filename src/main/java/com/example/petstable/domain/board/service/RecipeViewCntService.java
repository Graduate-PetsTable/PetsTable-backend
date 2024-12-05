package com.example.petstable.domain.board.service;

import com.example.petstable.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j

public class RecipeViewCntService {

    private final RedisTemplate<String, String> redisTemplateForCluster;
    private static final String KEY_PREFIX = "recipe-views";
    private final BoardRepository boardRepository;

    @Transactional
    public void incrementViewCount(Long postId) {
        String key = KEY_PREFIX + ":" + postId;
        ValueOperations<String, String> valueOperations = redisTemplateForCluster.opsForValue();
        if (valueOperations.get(key) == null) {
            valueOperations.set(key, String.valueOf(boardRepository.findViewCntByPostId(postId)), Duration.ofMinutes(5));
        }
        valueOperations.increment(key);
        log.info("value:{}",valueOperations.get(key));
    }

    @Scheduled(cron = "0 0/15 * * * ?") // 15분에 한 번씩 조회수 갱신
    public void deleteViewCntCacheFromRedis() {
        ScanOptions scanOptions = ScanOptions.scanOptions().match(KEY_PREFIX + "*").count(100).build(); // 100개
        Cursor<byte[]> keys = redisTemplateForCluster.getConnectionFactory()
                .getConnection()
                .scan(scanOptions);
        while (keys.hasNext()) {
            String data = Arrays.toString(keys.next());
            Long postId = Long.parseLong(data.split(":")[1]);
            String value = redisTemplateForCluster.opsForValue().get(data);
            if(value != null) {
                int viewCnt = Integer.parseInt(value);
                boardRepository.addViewCntFromRedis(postId, viewCnt); // RDB에 반영
                redisTemplateForCluster.delete(data); // Redis 키 삭제
                log.info("Synced and deleted Redis key: {}", data);
            } else {
                log.info("No data found for key: {}", data);
            }
        }
    }
}