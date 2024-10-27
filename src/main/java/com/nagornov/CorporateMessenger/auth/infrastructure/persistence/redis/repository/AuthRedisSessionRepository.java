package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.mapper.AuthRedisSessionMapper;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.model.AuthRedisSessionEntity;
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
public class AuthRedisSessionRepository {

    private final RedisRepository redisRepository;
    private final AuthRedisSessionMapper redisSessionMapper;

    private final static String PREFIX = RedisPrefix.SESSIONS.getPrefix();

    public void save(@NotNull AuthSession session, @NotNull Integer ttl, @NotNull TimeUnit unit) {

        String key = PREFIX + session.getId();
        Map<Object, Object> hash = redisSessionMapper.toEntity(session).toHash();

        redisRepository.setHash(key, hash);
        redisRepository.setExpire(key, ttl, unit);
    }

    public void delete(@NotNull AuthSession session) {
        String key = PREFIX + session.getId();
        redisRepository.delete(key);
    }

    public Optional<AuthSession> findSessionById(@NotNull UUID id) {

        String key = PREFIX + id;
        Map<Object, Object> hash = redisRepository.getHash(key);

        if (hash.isEmpty()) {
            return Optional.empty();
        }

        AuthSession domain = redisSessionMapper.toDomain(
                AuthRedisSessionEntity.fromHash(hash)
        );
        return Optional.of(domain);
    }

}
