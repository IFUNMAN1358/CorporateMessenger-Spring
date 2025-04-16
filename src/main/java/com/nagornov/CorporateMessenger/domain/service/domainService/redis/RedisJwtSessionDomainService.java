package com.nagornov.CorporateMessenger.domain.service.domainService.redis;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtSession;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisJwtSessionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisJwtSessionDomainService {

    private final RedisJwtSessionRepository redisJwtSessionRepository;

    public void saveByKeyExpire(
            @NonNull UUID userId,
            @NonNull JwtSession jwtSession,
            long timeout,
            TimeUnit timeUnit
    ) {
        try {
            redisJwtSessionRepository.saveByKeyExpire(userId, jwtSession, timeout, timeUnit);
        } catch (Exception e) {
            throw new ResourceConflictException(e.getMessage());
        }
    }

    public void deleteByKey(@NonNull UUID userId) {
        try {
            redisJwtSessionRepository.deleteByKey(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public Optional<JwtSession> findByKey(@NonNull UUID userId) {
        try {
            return redisJwtSessionRepository.findByKey(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

}
