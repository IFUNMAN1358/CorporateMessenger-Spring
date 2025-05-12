package com.nagornov.CorporateMessenger.domain.service.auth;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.ExternalService;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaExternalServiceRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalServiceService {

    private final JpaExternalServiceRepository jpaExternalServiceRepository;

    //@Cacheable(value = "external-services-optional", key = "{#name}")
    public Optional<ExternalService> findByName(@NonNull String name) {
        return jpaExternalServiceRepository.findByName(name);
    }

    //@Cacheable(value = "external-services-optional", key = "{#name}")
    public Optional<ExternalService> findByNameAndApiKey(@NonNull String name, @NonNull String apiKey) {
        return jpaExternalServiceRepository.findByNameAndApiKey(name, apiKey);
    }

    //@Cacheable(value = "external-services", key = "{#name}")
    public ExternalService getByName(@NonNull String name) {
        return jpaExternalServiceRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("ExternalService by name not found"));
    }

    //@Cacheable(value = "external-services", key = "{#name}")
    public ExternalService getByNameAndApiKey(@NonNull String name, @NonNull String apiKey) {
        return jpaExternalServiceRepository.findByNameAndApiKey(name, apiKey)
                .orElseThrow(() -> new ResourceNotFoundException("ExternalService by name and apiKey not found"));
    }

}