package com.nagornov.CorporateMessenger.auth.domain.service;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.repository.AuthRedisUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthRedisUserService {

    private final AuthRedisUserRepository redisUserRepository;

    public void createUser(@NotNull AuthUser user, @NotNull Integer ttl, @NotNull TimeUnit unit) {
        redisUserRepository.save(user, ttl, unit);
    }

    public void deleteSession(@NotNull AuthUser user) {
        redisUserRepository.delete(user);
    }

    public Optional<AuthUser> findUserById(@NotNull UUID id) {
        return redisUserRepository.findUserById(id);
    }

}
