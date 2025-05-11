package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisSessionEntity;
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

    private final RedisTemplate<String, RedisSessionEntity> redisSessionTemplate;
    private final RedisSessionMapper redisSessionMapper;

    public void saveByKeyExpire(UUID userId, Session session, long timeout, TimeUnit timeUnit) {
        String sessionKey = RedisKeyUtils.sessionKey(userId);

        redisSessionTemplate.opsForHash().putAll(
                sessionKey,
                redisSessionMapper.toEntity(session).toMap()
        );
        redisSessionTemplate.expire(sessionKey, timeout, timeUnit);
    }

    public void deleteByKey(UUID userId) {
        String sessionKey = RedisKeyUtils.sessionKey(userId);
        redisSessionTemplate.delete(sessionKey);
    }

    public Optional<Session> findByKey(UUID userId) {
        String sessionKey = RedisKeyUtils.sessionKey(userId);
        Map<Object, Object> hash = redisSessionTemplate.opsForHash().entries(sessionKey);

        if (hash.isEmpty()) {
            return Optional.empty();
        }
        Session session = redisSessionMapper.toDomain(
                RedisSessionEntity.fromMap(hash)
        );
        return Optional.of(session);
    }

}
