package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.domain.model.Session;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.common.RedisUtils;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper.RedisSessionMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.common.RedisRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisSessionRepository {

    private final RedisRepository redisRepository;
    private final RedisSessionMapper redisSessionMapper;

    public Session create(@NotNull UUID userId, @NotNull Session session, @NotNull int timeout, @NotNull TimeUnit timeUnit) {
        final String userSessionKey = RedisUtils.userSessionKey(userId);
        final Map<Object, Object> sessionHash = redisSessionMapper.toHash(session);

        redisRepository.setHash(userSessionKey, sessionHash);
        redisRepository.setExpire(userSessionKey, timeout, timeUnit);
        return session;
    }

    public void delete(@NotNull UUID userId) {
        final String userSessionKey = RedisUtils.userSessionKey(userId);
        redisRepository.delete(userSessionKey);
    }

    public Optional<Session> findById(@NotNull UUID userId) {
        final String userSessionKey = RedisUtils.userSessionKey(userId);
        Map<Object, Object> hash = redisRepository.getHash(userSessionKey);

        if (hash.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(redisSessionMapper.fromHash(hash));
    }

}
