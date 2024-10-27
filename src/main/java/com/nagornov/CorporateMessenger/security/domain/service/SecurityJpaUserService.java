package com.nagornov.CorporateMessenger.security.domain.service;

import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.repository.SecurityJpaUserRepository;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa.JpaUserNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityJpaUserService {

    private final SecurityJpaUserRepository jpaUserRepository;

    public SecurityUser getUserById(@NotNull UUID id) {
        return jpaUserRepository.findUserById(id)
                .orElseThrow(JpaUserNotFoundException::new);
    }

}
