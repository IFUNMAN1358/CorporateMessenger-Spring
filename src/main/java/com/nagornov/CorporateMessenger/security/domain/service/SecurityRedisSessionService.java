package com.nagornov.CorporateMessenger.security.domain.service;

import com.nagornov.CorporateMessenger.security.domain.model.SecuritySession;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.repository.SecurityRedisSessionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SecurityRedisSessionService {

    private final SecurityRedisSessionRepository redisSessionRepository;

    public void createSession(@NotNull SecuritySession session, @NotNull Integer ttl, @NotNull TimeUnit unit) {
        redisSessionRepository.save(session, ttl, unit);
    }

    public void deleteSession(@NotNull SecuritySession session) {
        redisSessionRepository.delete(session);
    }

    public Optional<SecuritySession> findSessionById(@NotNull UUID id) {
        return redisSessionRepository.findSessionById(id);
    }

}
