package com.smartqueue.queueservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class RedisConfig {

    // ── Caffeine Cache — للـ position data (10 ثواني TTL) ───
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager =
                new CaffeineCacheManager("queuePosition");
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(10, TimeUnit.SECONDS)
                        .maximumSize(1000)
        );
        return manager;
    }

    // ── Redis Template — للـ WebSocket وغيره ────────────────
    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory factory) {
        RedisTemplate<String, String> template =
                new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}