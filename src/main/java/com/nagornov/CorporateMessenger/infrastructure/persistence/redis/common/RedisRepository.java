package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.common;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<Object, Object> redisTemplate;

    public void setValue(@NotNull String key, @NotNull Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setHash(@NotNull String key, @NotNull Map<Object, Object> hash) {
        redisTemplate.opsForHash().putAll(key, hash);
    }

    public void setHashField(@NotNull String key, @NotNull String field, @NotNull Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Object getValue(@NotNull String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Map<Object, Object> getHash(@NotNull String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void delete(@NotNull String key) {
        redisTemplate.delete(key);
    }

    public void deleteHashField(@NotNull String key, @NotNull String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    public void setExpire(@NotNull String key, @NotNull long timeout, @NotNull TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }
}