package com.nagornov.CorporateMessenger.security.domain.service;

import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.repository.SecurityRedisUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SecurityRedisUserService {

    private final SecurityRedisUserRepository redisUserRepository;

    public void createUser(@NotNull SecurityUser user, @NotNull Integer ttl, @NotNull TimeUnit unit) {
        redisUserRepository.save(user, ttl, unit);
    }

    public void deleteSession(@NotNull SecurityUser user) {
        redisUserRepository.delete(user);
    }

    public Optional<SecurityUser> findUserById(@NotNull UUID id) {
        return redisUserRepository.findUserById(id);
    }

}
