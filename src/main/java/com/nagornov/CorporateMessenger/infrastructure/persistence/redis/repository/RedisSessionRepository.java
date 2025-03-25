package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper.RedisSessionMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.utils.RedisKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisSessionRepository {

    private final RedisTemplate<String, Session> redisSessionTemplate;
    private final RedisSessionMapper redisSessionMapper;

    public Session create(UUID userId, Session session, int timeout, TimeUnit timeUnit) {
        String userSessionKey = RedisKeyUtils.sessionKey(userId);
        Map<Object, Object> sessionHash = redisSessionMapper.toHash(session);

        redisSessionTemplate.opsForHash().putAll(userSessionKey, sessionHash);
        redisSessionTemplate.expire(userSessionKey, timeout, timeUnit);
        return session;
    }

    public void delete(UUID userId) {

        String userSessionKey = RedisKeyUtils.sessionKey(userId);

        redisSessionTemplate.delete(userSessionKey);
    }

    public Optional<Session> findById(UUID userId) {
        String userSessionKey = RedisKeyUtils.sessionKey(userId);
        Map<Object, Object> hash = redisSessionTemplate.opsForHash().entries(userSessionKey);

        if (hash.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(redisSessionMapper.fromHash(hash));
    }

}
