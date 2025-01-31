package com.nagornov.CorporateMessenger.domain.service.domainService.redis;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.Session;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisSessionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisSessionDomainService {

    private final RedisSessionRepository redisSessionRepository;

    public Session save(@NotNull UUID userId, @NotNull Session session) {
        return redisSessionRepository.create(userId, session, 48, TimeUnit.HOURS);
    }

    public void deleteByUserId(@NotNull UUID userId) {
        redisSessionRepository.delete(userId);
    }

    public Session getById(@NotNull UUID userId) {
        return redisSessionRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Session with this id not found"));
    }

}
