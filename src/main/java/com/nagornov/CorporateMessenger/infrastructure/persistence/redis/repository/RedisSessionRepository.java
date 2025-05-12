package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.domain.enums.redis.RedisPrefix;
import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisSessionEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper.RedisSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisSessionRepository {

    private final static String SESSION = RedisPrefix.SESSION.getPrefix();

    private final RedisTemplate<String, RedisSessionEntity> redisSessionTemplate;
    private final RedisSessionMapper redisSessionMapper;

    public void saveExpire(
            UUID userId,
            String externalServiceName,
            Session session,
            long timeout,
            TimeUnit timeUnit
    ) {
        String key = "%s:%s".formatted(SESSION, userId);
        String field = "%s:%s:%s".formatted(SESSION, userId, externalServiceName);

        redisSessionTemplate.opsForHash().put(
                key,
                field,
                redisSessionMapper.toEntity(session).toMap()
        );
        redisSessionTemplate.expire(key, timeout, timeUnit);
    }

    public void delete(UUID userId, String externalServiceName) {
        String key = "%s:%s".formatted(SESSION, userId);
        String field = "%s:%s:%s".formatted(SESSION, userId, externalServiceName);
        redisSessionTemplate.opsForHash().delete(key, field);
    }

    public void deleteAll(UUID userId) {
        String key = "%s:%s".formatted(SESSION, userId);
        redisSessionTemplate.delete(key);
    }

    public Optional<Session> find(UUID userId, String externalServiceName) {
        String key = "%s:%s".formatted(SESSION, userId);
        String field = "%s:%s:%s".formatted(SESSION, userId, externalServiceName);

        RedisSessionEntity entity = (RedisSessionEntity) redisSessionTemplate.opsForHash().get(key, field);

        if (entity == null) {
            return Optional.empty();
        }

        Session session = redisSessionMapper.toDomain(entity);
        return Optional.of(session);
    }

    public Map<String, Session> findAll(UUID userId) {
        String key = "%s:%s".formatted(SESSION, userId);

        Map<Object, Object> entries = redisSessionTemplate.opsForHash().entries(key);
        Map<String, Session> sessions = new HashMap<>();

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            RedisSessionEntity entity = (RedisSessionEntity) entry.getValue();
            sessions.put((String) entry.getKey(), redisSessionMapper.toDomain(entity));
        }
        return sessions;
    }

}
