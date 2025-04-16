package com.nagornov.CorporateMessenger.domain.service.domainService.redis;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.repository.RedisCsrfRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCsrfDomainService {

    private final RedisCsrfRepository redisCsrfRepository;

    public void saveExpire(
            @NonNull String csrfToken,
            long timeout,
            @NonNull TimeUnit timeUnit
    ) {
        try {
            redisCsrfRepository.saveExpire(csrfToken, timeout, timeUnit);
        } catch (Exception e) {
            throw new ResourceConflictException("Error while saving csrf token to redis");
        }
    }

    public void delete(@NonNull String csrfToken) {
        try {
            redisCsrfRepository.delete(csrfToken);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error while saving csrf token to redis");
        }
    }

    public boolean exists(@NonNull String csrfToken) {
        try {
            return redisCsrfRepository.exists(csrfToken);
        } catch (Exception e) {
            throw new RuntimeException("Error while check csrf token exists in redis", e.getCause());
        }
    }

}
