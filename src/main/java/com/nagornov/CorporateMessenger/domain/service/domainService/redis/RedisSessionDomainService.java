package com.nagornov.CorporateMessenger.domain.service.domainService.redis;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisSessionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisSessionDomainService {

    private final RedisSessionRepository redisSessionRepository;

    public Session save(@NonNull UUID userId, @NonNull Session session) {
        return redisSessionRepository.create(userId, session, 48, TimeUnit.HOURS);
    }

    public void deleteByUserId(@NonNull UUID userId) {
        redisSessionRepository.delete(userId);
    }

    public Session getById(@NonNull UUID userId) {
        return redisSessionRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Session[id=%s] not found".formatted(userId)));
    }

}
