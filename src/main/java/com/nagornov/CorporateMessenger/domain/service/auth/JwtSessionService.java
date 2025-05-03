package com.nagornov.CorporateMessenger.domain.service.auth;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtSession;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisJwtSessionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtSessionService {

    private final RedisJwtSessionRepository redisJwtSessionRepository;

    public JwtSession saveToRedis(
            @NonNull UUID userId,
            @NonNull String accessToken,
            @NonNull String refreshToken,
            long timeout,
            @NonNull TimeUnit timeUnit
    ) {
        try {
            JwtSession jwtSession = new JwtSession(
                    accessToken,
                    refreshToken,
                    Instant.now(),
                    Instant.now()
            );
            redisJwtSessionRepository.saveByKeyExpire(userId, jwtSession, timeout, timeUnit);
            return jwtSession;
        } catch (Exception e) {
            throw new ResourceConflictException(e.getMessage());
        }
    }

    public void deleteFromRedis(@NonNull UUID userId) {
        try {
            redisJwtSessionRepository.deleteByKey(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public Optional<JwtSession> findInRedisByUserId(@NonNull UUID userId) {
        try {
            return redisJwtSessionRepository.findByKey(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

}
