package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository;

import com.nagornov.CorporateMessenger.domain.enums.redis.RedisPrefix;
import com.nagornov.CorporateMessenger.domain.model.auth.ExternalService;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisExternalServiceEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper.RedisExternalServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisExternalServiceRepository {

    private static final String REDIS_EXTERNAL_SERVICES_PREFIX = RedisPrefix.EXTERNAL_SERVICES.getPrefix();

    private final RedisTemplate<String, RedisExternalServiceEntity> redisExternalServiceTemplate;
    private final RedisExternalServiceMapper redisExternalServiceMapper;


    public void saveExpire(
            String externalServiceName,
            ExternalService externalService,
            long timeout,
            TimeUnit timeUnit
    ) {
        String field = "%s:%s".formatted(REDIS_EXTERNAL_SERVICES_PREFIX, externalServiceName);

        redisExternalServiceTemplate.opsForHash().put(
                REDIS_EXTERNAL_SERVICES_PREFIX,
                field,
                redisExternalServiceMapper.toEntity(externalService)
        );
        redisExternalServiceTemplate.expire(REDIS_EXTERNAL_SERVICES_PREFIX, timeout, timeUnit);
    }


    public void delete(String externalServiceName) {
        String field = "%s:%s".formatted(REDIS_EXTERNAL_SERVICES_PREFIX, externalServiceName);
        redisExternalServiceTemplate.opsForHash().delete(REDIS_EXTERNAL_SERVICES_PREFIX, field);
    }


    public Optional<ExternalService> findByName(String externalServiceName) {
        String field = "%s:%s".formatted(REDIS_EXTERNAL_SERVICES_PREFIX, externalServiceName);

        Object entity = redisExternalServiceTemplate.opsForHash().get(
                REDIS_EXTERNAL_SERVICES_PREFIX,
                field
        );

        if (entity == null) {
            return Optional.empty();
        }
        ExternalService domain = redisExternalServiceMapper.toDomain((RedisExternalServiceEntity) entity);
        return Optional.of(domain);
    }


    public Optional<ExternalService> findByNameAndApiKey(String externalServiceName, String externalServiceApiKey) {
        String field = "%s:%s".formatted(REDIS_EXTERNAL_SERVICES_PREFIX, externalServiceName);

        Object hash = redisExternalServiceTemplate.opsForHash().get(
                REDIS_EXTERNAL_SERVICES_PREFIX,
                field
        );

        if (hash == null) {
            return Optional.empty();
        }
        ExternalService domain = redisExternalServiceMapper.toDomain((RedisExternalServiceEntity) hash);

        if (!domain.getApiKey().equals(externalServiceApiKey)) {
            return Optional.empty();
        }
        return Optional.of(domain);
    }


    public List<ExternalService> findAll() {
        Map<Object, Object> hash = redisExternalServiceTemplate.opsForHash().entries(REDIS_EXTERNAL_SERVICES_PREFIX);
        List<ExternalService> externalServices = new ArrayList<>();

        for (Map.Entry<Object, Object> entity : hash.entrySet()) {
            ExternalService domain = redisExternalServiceMapper.toDomain((RedisExternalServiceEntity) entity.getValue());
            externalServices.add(domain);
        }
        return externalServices;
    }
}