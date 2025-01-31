package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.RegistrationKey;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaRegistrationKeyRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaRegistrationKeyDomainService {

    private final JpaRegistrationKeyRepository jpaRegistrationKeyRepository;

    public void save(@NotNull RegistrationKey key) {
        jpaRegistrationKeyRepository.findByValue(key.getValue())
                .ifPresent(e -> {
                    throw new RuntimeException("Registration key with this value already exists");
                });
        jpaRegistrationKeyRepository.save(key);
    }

    public void update(@NotNull RegistrationKey key) {
        jpaRegistrationKeyRepository.findByValue(key.getValue())
                .orElseThrow(() -> new RuntimeException("Registration key with this value already exists"));
        jpaRegistrationKeyRepository.save(key);
    }

    public RegistrationKey getByValue(@NotNull String value) {
        return jpaRegistrationKeyRepository.findByValue(value)
                .orElseThrow(() -> new ResourceNotFoundException("Registration key with this value not found"));
    }

}
