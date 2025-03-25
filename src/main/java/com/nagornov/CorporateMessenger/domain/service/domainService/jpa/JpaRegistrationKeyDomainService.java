package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaRegistrationKeyRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaRegistrationKeyDomainService {

    private final JpaRegistrationKeyRepository jpaRegistrationKeyRepository;

    public void save(@NonNull RegistrationKey key) {
        jpaRegistrationKeyRepository.save(key);
    }

    public void delete(@NonNull RegistrationKey key) {
        jpaRegistrationKeyRepository.delete(key);
    }

    public void deleteById(@NonNull UUID id) {
        jpaRegistrationKeyRepository.deleteById(id);
    }

    public RegistrationKey getByValue(@NonNull String value) {
        return jpaRegistrationKeyRepository.findByValue(value)
                .orElseThrow(() -> new ResourceNotFoundException("Registration key with this value not found"));
    }

    public Optional<RegistrationKey> findByValue(@NonNull String value) {
        return jpaRegistrationKeyRepository.findByValue(value);
    }

    public void apply(@NonNull RegistrationKey registrationKey, @NonNull UUID userId) {
        registrationKey.markAsApplied();
        registrationKey.initUserId(userId);
        save(registrationKey);
    }

}
