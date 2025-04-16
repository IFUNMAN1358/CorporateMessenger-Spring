package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtSession;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisJwtSessionEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper.RedisJwtSessionMapper;
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
public class RedisJwtSessionRepository {

    private final RedisTemplate<String, RedisJwtSessionEntity> redisSessionTemplate;
    private final RedisJwtSessionMapper redisJwtSessionMapper;

    public void saveByKeyExpire(UUID userId, JwtSession jwtSession, long timeout, TimeUnit timeUnit) {
        String jwtSessionKey = RedisKeyUtils.jwtSessionKey(userId);

        redisSessionTemplate.opsForHash().putAll(
                jwtSessionKey,
                redisJwtSessionMapper.toEntity(jwtSession).toMap()
        );
        redisSessionTemplate.expire(jwtSessionKey, timeout, timeUnit);
    }

    public void deleteByKey(UUID userId) {
        String jwtSessionKey = RedisKeyUtils.jwtSessionKey(userId);
        redisSessionTemplate.delete(jwtSessionKey);
    }

    public Optional<JwtSession> findByKey(UUID userId) {
        String jwtSessionKey = RedisKeyUtils.jwtSessionKey(userId);
        Map<Object, Object> hash = redisSessionTemplate.opsForHash().entries(jwtSessionKey);

        if (hash.isEmpty()) {
            return Optional.empty();
        }
        JwtSession jwtSession = redisJwtSessionMapper.toDomain(
                RedisJwtSessionEntity.fromMap(hash)
        );
        return Optional.of(jwtSession);
    }

}
