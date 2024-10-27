package com.nagornov.CorporateMessenger.auth.domain.service;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.repository.AuthRedisSessionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthRedisSessionService {

    private final AuthRedisSessionRepository redisSessionRepository;

    public void createSession(@NotNull AuthSession session, @NotNull Integer ttl, @NotNull TimeUnit unit) {
        redisSessionRepository.save(session, ttl, unit);
    }

    public void deleteSession(@NotNull AuthSession session) {
        redisSessionRepository.delete(session);
    }

    public Optional<AuthSession> findSessionById(@NotNull UUID id) {
        return redisSessionRepository.findSessionById(id);
    }

}
