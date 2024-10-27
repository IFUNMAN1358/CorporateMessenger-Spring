package com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.mapper.SecurityRedisUserMapper;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.model.SecurityRedisUserEntity;
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
public class SecurityRedisUserRepository {

    private final RedisRepository redisRepository;
    private final SecurityRedisUserMapper redisUserMapper;

    private static final String PREFIX = RedisPrefix.USERS.getPrefix();

    public void save(@NotNull SecurityUser user, @NotNull Integer ttl, @NotNull TimeUnit unit) {
        String key = PREFIX + user.getId();
        Map<Object, Object> hash = redisUserMapper.toEntity(user).toHash();

        redisRepository.setHash(key, hash);
        redisRepository.setExpire(key, ttl, unit);
    }

    public void delete(@NotNull SecurityUser user) {
        String userId = String.valueOf(user.getId());
        redisRepository.delete(userId);
    }

    public Optional<SecurityUser> findUserById(@NotNull UUID id) {

        String key = PREFIX + id;
        Map<Object, Object> hash = redisRepository.getHash(key);

        if (hash.isEmpty()) {
            return Optional.empty();
        }

        SecurityUser domain = redisUserMapper.toDomain(
                SecurityRedisUserEntity.fromHash(hash)
        );
        return Optional.of(domain);
    }

}
