package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import com.nagornov.CorporateMessenger.domain.utils.RandomStringGenerator;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaRegistrationKeyRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationKeyService {

    private final JpaRegistrationKeyRepository jpaRegistrationKeyRepository;

    @Transactional
    public RegistrationKey create() {
        RegistrationKey key = new RegistrationKey(
                UUID.randomUUID(),
                null,
                RandomStringGenerator.generateRandomString(12),
                false,
                Instant.now()
        );
        return jpaRegistrationKeyRepository.save(key);
    }

    @Transactional
    public void delete(@NonNull RegistrationKey key) {
        jpaRegistrationKeyRepository.delete(key);
    }

    public RegistrationKey getByValue(@NonNull String value) {
        return jpaRegistrationKeyRepository.findByValue(value)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RegistrationKey[value=%s] not found".formatted(value),
                        List.of(
                                new FieldError("registrationKey", "Ключ регистрации с таким значением не найден"))
                        )
                );
    }

    public Optional<RegistrationKey> findById(@NonNull UUID id) {
        return jpaRegistrationKeyRepository.findById(id);
    }

    public Optional<RegistrationKey> findByValue(@NonNull String value) {
        return jpaRegistrationKeyRepository.findByValue(value);
    }

    public List<RegistrationKey> findAllSortedByNotApplied(int page, int size) {
        return jpaRegistrationKeyRepository.findAllSortedByNotApplied(page, size);
    }

    public RegistrationKey getById(@NonNull UUID id) {
        return jpaRegistrationKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegistrationKey[id=%s] not found".formatted(id)));
    }

    @Transactional
    public void apply(@NonNull RegistrationKey registrationKey, @NonNull UUID userId) {
        registrationKey.markAsApplied();
        registrationKey.initUserId(userId);
        jpaRegistrationKeyRepository.save(registrationKey);
    }

}