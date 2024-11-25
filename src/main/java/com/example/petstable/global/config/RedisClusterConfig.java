package com.example.petstable.global.config;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@Slf4j
public class RedisClusterConfig {
    @Value("${spring.cluster-redis.node1.host}")
    private String node1;
    @Value("${spring.cluster-redis.node2.host}")
    private String node2;
    @Value("${spring.cluster-redis.node3.host}")
    private String node3;
    @Value("${spring.cluster-redis.node4.host}")
    private String node4;
    @Value("${spring.cluster-redis.node5.host}")
    private String node5;
    @Value("${spring.cluster-redis.node6.host}")
    private String node6;

    @Value("${spring.data.redis.primary.password}")
    private String password;

    @Bean("redisConnectionFactoryForPredixy")
    @Primary
    public LettuceConnectionFactory redisConnectionFactoryForCluster() {
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
        clusterConfig.clusterNode(node1, 7001);
        clusterConfig.clusterNode(node2, 7002);
        clusterConfig.clusterNode(node3, 7003);
        clusterConfig.clusterNode(node4, 7101);
        clusterConfig.clusterNode(node5, 7102);
        clusterConfig.clusterNode(node6, 7103);
        clusterConfig.setPassword(RedisPassword.of(password));
        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofSeconds(5L))
                .tcpNoDelay(true)
                .keepAlive(true)
                .build();

        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions
                .builder()
                .dynamicRefreshSources(true)
                .enableAllAdaptiveRefreshTriggers()
                .enablePeriodicRefresh() // 60초마다 refresh
                .refreshTriggersReconnectAttempts(3) // 재연결 시도 후 갱신
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions
                .builder()
                .socketOptions(socketOptions)
                .pingBeforeActivateConnection(true) // 연결 활성화 전에 ping
                .autoReconnect(true)
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .validateClusterNodeMembership(false)
                .nodeFilter(it ->
                        ! (it.is(RedisClusterNode.NodeFlag.FAIL)
                                || it.is(RedisClusterNode.NodeFlag.EVENTUAL_FAIL)
                                || it.is(RedisClusterNode.NodeFlag.HANDSHAKE)
                                || it.is(RedisClusterNode.NodeFlag.NOADDR)))
                .maxRedirects(3).build();

        final LettuceClientConfiguration clientConfig = LettuceClientConfiguration
                .builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .commandTimeout(Duration.ofSeconds(5L)) // 명령 타임아웃 5초로 설정
                .clientOptions(clusterClientOptions)
                .build();

        clusterConfig.setMaxRedirects(3);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(clusterConfig, clientConfig);
        factory.setValidateConnection(false);
        factory.setShareNativeConnection(true);

        return factory;
    }

    @Bean("redisTemplateForCluster")
    @Primary
    public RedisTemplate<String, String> redisTemplateForCluster() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactoryForCluster());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean("cacheManagerForCluster")
    @Primary
    public CacheManager cacheManagerForCluster() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(60L));
        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactoryForCluster())
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
