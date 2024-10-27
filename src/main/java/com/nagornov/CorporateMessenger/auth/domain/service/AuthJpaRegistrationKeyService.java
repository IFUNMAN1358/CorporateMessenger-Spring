package com.nagornov.CorporateMessenger.auth.domain.service;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthRegistrationKey;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.repository.AuthJpaRegistrationKeyRepository;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa.JpaRegistrationKeyAlreadyAppliedException;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa.JpaRegistrationKeyNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthJpaRegistrationKeyService {

    private final AuthJpaRegistrationKeyRepository jpaRegistrationKeyRepository;

    public void validateAndApplyRegistrationKey(@NotNull String value) {

        AuthRegistrationKey existingKey = jpaRegistrationKeyRepository.findRegistrationKeyByValue(value)
                .orElseThrow(JpaRegistrationKeyNotFoundException::new);

        if (existingKey.getIsApplied()) {
            throw new JpaRegistrationKeyAlreadyAppliedException();
        }

        AuthRegistrationKey savingKey = new AuthRegistrationKey(
                existingKey.getId(),
                existingKey.getValue(),
                true,
                existingKey.getCreatedAt()
        );
        jpaRegistrationKeyRepository.save(savingKey);
    }
}
