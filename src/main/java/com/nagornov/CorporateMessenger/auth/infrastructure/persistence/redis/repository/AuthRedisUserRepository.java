package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.mapper.AuthRedisUserMapper;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.model.AuthRedisUserEntity;
import com.nagornov.CorporateMessenger.sharedKernel.db.redis.RedisPrefix;
import com.nagornov.CorporateMessenger.sharedKernel.db.redis.RedisRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AuthRedisUserRepository {

    private final RedisRepository redisRepository;
    private final AuthRedisUserMapper redisUserMapper;

    private final static String PREFIX = RedisPrefix.USERS.getPrefix();

    public void save(@NotNull AuthUser user, @NotNull Integer ttl, @NotNull TimeUnit unit) {
        String key = PREFIX + user.getId();
        Map<Object, Object> hash = redisUserMapper.toEntity(user).toHash();

        redisRepository.setHash(key, hash);
        redisRepository.setExpire(key, ttl, unit);
    }

    public void delete(@NotNull AuthUser user) {
        String userId = String.valueOf(user.getId());
        redisRepository.delete(userId);
    }

    public Optional<AuthUser> findUserById(@NotNull UUID id) {

        String key = PREFIX + id;
        Map<Object, Object> hash = redisRepository.getHash(key);

        if (hash.isEmpty()) {
            return Optional.empty();
        }

        AuthUser domain = redisUserMapper.toDomain(
                AuthRedisUserEntity.fromHash(hash)
        );
        return Optional.of(domain);
    }

}
