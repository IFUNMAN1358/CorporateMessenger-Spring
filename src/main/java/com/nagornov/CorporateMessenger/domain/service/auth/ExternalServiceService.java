package com.nagornov.CorporateMessenger.domain.service.auth;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.ExternalService;
import com.nagornov.CorporateMessenger.domain.utils.RandomStringGenerator;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.ExternalServiceProperties;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaExternalServiceRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisExternalServiceRepository;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExternalServiceService {

    private final ExternalServiceProperties externalServiceProperties;
    private final JpaExternalServiceRepository jpaExternalServiceRepository;
    private final RedisExternalServiceRepository redisExternalServiceRepository;


    public ExternalService create(@NonNull String serviceName, @NotNull Boolean requiredApiKey) {
        ExternalService externalService = new ExternalService(
                UUID.randomUUID(),
                serviceName,
                requiredApiKey ? RandomStringGenerator.generateRandomString(64) : null,
                requiredApiKey
        );
        redisExternalServiceRepository.saveExpire(
                externalService.getName(),
                externalService,
                externalServiceProperties.getRedis().getTimeout(),
                externalServiceProperties.getRedis().getTimeUnitSeconds()
        );
        return jpaExternalServiceRepository.save(externalService);
    }


    public ExternalService updateWithNewGeneratedApiKey(@NonNull ExternalService externalService) {
        if (!externalService.isRequiresApiKey()) {
            throw new ResourceConflictException(
                    "it is impossible to generate a new api key for an external service that does not require an api key"
            );
        }
        externalService.updateApiKey(RandomStringGenerator.generateRandomString(64));
        redisExternalServiceRepository.saveExpire(
                externalService.getName(),
                externalService,
                externalServiceProperties.getRedis().getTimeout(),
                externalServiceProperties.getRedis().getTimeUnitSeconds()
        );
        return jpaExternalServiceRepository.save(externalService);
    }


    public void delete(@NonNull ExternalService externalService) {
        redisExternalServiceRepository.delete(externalService.getName());
        jpaExternalServiceRepository.delete(externalService);
    }


    public Optional<ExternalService> findByName(@NonNull String name) {
        Optional<ExternalService> optRedisExternalService = redisExternalServiceRepository.findByName(name);

        if (optRedisExternalService.isEmpty()) {
            Optional<ExternalService> optJpaRedisExternalService = jpaExternalServiceRepository.findByName(name);

            if (optJpaRedisExternalService.isEmpty()) {
                return Optional.empty();
            }

            redisExternalServiceRepository.saveExpire(
                    name,
                    optJpaRedisExternalService.get(),
                    externalServiceProperties.getRedis().getTimeout(),
                    externalServiceProperties.getRedis().getTimeUnitSeconds()
            );
            return optJpaRedisExternalService;
        }
        return optRedisExternalService;
    }


    public Optional<ExternalService> findByNameAndApiKey(@NonNull String name, @NonNull String apiKey) {
        Optional<ExternalService> optRedisExternalService = redisExternalServiceRepository.findByNameAndApiKey(name, apiKey);

        if (optRedisExternalService.isEmpty()) {
            Optional<ExternalService> optJpaRedisExternalService = jpaExternalServiceRepository.findByNameAndApiKey(name, apiKey);

            if (optJpaRedisExternalService.isEmpty()) {
                return Optional.empty();
            }

            redisExternalServiceRepository.saveExpire(
                    name,
                    optJpaRedisExternalService.get(),
                    externalServiceProperties.getRedis().getTimeout(),
                    externalServiceProperties.getRedis().getTimeUnitSeconds()
            );
            return optJpaRedisExternalService;
        }
        return optRedisExternalService;
    }


    public List<ExternalService> findAll() {
        List<ExternalService> redisExternalServices = redisExternalServiceRepository.findAll();

        if (redisExternalServices.isEmpty()) {
            return jpaExternalServiceRepository.findAll();
        }

        return redisExternalServices;
    }


    public ExternalService getByName(@NonNull String name) {
        return findByName(name).orElseThrow(() -> new ResourceNotFoundException("ExternalService by name not found"));
    }


    public ExternalService getByNameAndApiKey(@NonNull String name, @NonNull String apiKey) {
        return findByNameAndApiKey(name, apiKey).orElseThrow(() -> new ResourceNotFoundException("ExternalService by name and apiKey not found"));
    }
}