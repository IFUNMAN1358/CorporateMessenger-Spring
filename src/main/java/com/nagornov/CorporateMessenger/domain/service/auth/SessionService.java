package com.nagornov.CorporateMessenger.domain.service.auth;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisSessionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final RedisSessionRepository redisSessionRepository;

    public Session saveToRedis(
            @NonNull UUID userId,
            @NonNull String externalServiceName,
            @NonNull String accessToken,
            @NonNull String refreshToken,
            @NonNull String csrfToken,
            long timeout,
            @NonNull TimeUnit timeUnit
    ) {
        try {
            Session session = new Session(
                    accessToken,
                    refreshToken,
                    csrfToken,
                    Instant.now(),
                    Instant.now()
            );
            redisSessionRepository.saveExpire(userId, externalServiceName, session, timeout, timeUnit);
            return session;
        } catch (Exception e) {
            throw new ResourceConflictException(e.getMessage());
        }
    }

    public void deleteFromRedis(@NonNull UUID userId, @NonNull String externalServiceName) {
        try {
            redisSessionRepository.delete(userId, externalServiceName);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public void deleteAllFromRedis(@NonNull UUID userId) {
        try {
            redisSessionRepository.deleteAll(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public Optional<Session> findInRedis(@NonNull UUID userId, @NonNull String externalServiceName) {
        try {
            return redisSessionRepository.find(userId, externalServiceName);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public Map<String, Session> findAllInRedis(@NonNull UUID userId) {
        try {
            return redisSessionRepository.findAll(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

}
