package com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.security.domain.model.SecuritySession;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.mapper.SecurityRedisSessionMapper;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.model.SecurityRedisSessionEntity;
import com.nagornov.CorporateMessenger.sharedKernel.db.redis.RedisPrefix;
import com.nagornov.CorporateMessenger.sharedKernel.db.redis.RedisRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class SecurityRedisSessionRepository {

    private final RedisRepository redisRepository;
    private final SecurityRedisSessionMapper redisSessionMapper;

    private final static String PREFIX = RedisPrefix.SESSIONS.getPrefix();

    public void save(@NotNull SecuritySession session, @NotNull Integer ttl, @NotNull TimeUnit unit) {

        String key = PREFIX + session.getId();
        Map<Object, Object> hash = redisSessionMapper.toEntity(session).toHash();

        redisRepository.setHash(key, hash);
        redisRepository.setExpire(key, ttl, unit);
    }

    public void delete(@NotNull SecuritySession session) {
        String sessionId = String.valueOf(session.getId());
        redisRepository.delete(sessionId);
    }

    public Optional<SecuritySession> findSessionById(@NotNull UUID id) {

        String key = PREFIX + id;
        Map<Object, Object> hash = redisRepository.getHash(key);

        if (hash.isEmpty()) {
            return Optional.empty();
        }

        SecuritySession domain = redisSessionMapper.toDomain(
                SecurityRedisSessionEntity.fromHash(hash)
        );
        return Optional.of(domain);
    }

}
