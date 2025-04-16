package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisCsrfRepository {

    private final RedisTemplate<String, String> redisCsrfTemplate;

    public void saveExpire(String csrfToken, long timeout, TimeUnit timeUnit) {
        redisCsrfTemplate.opsForValue().set(csrfToken, "0");
        redisCsrfTemplate.expire(csrfToken, timeout, timeUnit);
    }

    public void delete(String csrfToken) {
        redisCsrfTemplate.delete(csrfToken);
    }

    public boolean exists(String csrfToken) {
        return Boolean.TRUE.equals(redisCsrfTemplate.hasKey(csrfToken));
    }

}
