package com.example.petstable.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Value("${spring.data.redis.primary.host}")
    private String host;
    @Value("${spring.data.redis.primary.port}")
    private int port;
    @Value("${spring.data.redis.primary.password}")
    private String password;

    @Bean("redisConnectionFactoryForOne")
    public RedisConnectionFactory redisConnectionFactoryForOne() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }

    @Bean("redisTemplateForOne")
    public RedisTemplate<String, Object> redisTemplateForOne() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactoryForOne());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean("oauthPublicKeyCacheManager")
    public CacheManager oauthPublicKeyCacheManager() {
        /*
         * public key 갱신은 1년에 몇 번 안되므로 ttl 3일로 설정
         * 유저가 하루 1번 로그인한다고 가정, 최소 1일은 넘기는 것이 좋다고 판단
         */
        RedisCacheConfiguration redisCacheConfiguration = generateCacheConfiguration()
                .entryTtl(Duration.ofDays(3L));
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactoryForOne())
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    @Bean("memberCacheManager")
    public CacheManager memberCacheManager() {
        RedisCacheConfiguration redisCacheConfiguration = generateCacheConfiguration()
                .entryTtl(Duration.ofMinutes(60L));
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactoryForOne())
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    private RedisCacheConfiguration generateCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()));
    }
}
