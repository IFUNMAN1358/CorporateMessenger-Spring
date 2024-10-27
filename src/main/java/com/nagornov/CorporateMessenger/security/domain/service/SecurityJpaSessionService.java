package com.nagornov.CorporateMessenger.security.domain.service;

import com.nagornov.CorporateMessenger.security.domain.model.SecuritySession;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.repository.SecurityJpaSessionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityJpaSessionService {

    private final SecurityJpaSessionRepository jpaSessionRepository;

    public Optional<SecuritySession> findSessionById(@NotNull UUID id) {
        return jpaSessionRepository.findSessionById(id);
    }

    public Boolean existsSessionByAccessToken(@NotNull String accessToken) {
        return jpaSessionRepository
                .existsSessionByAccessToken(accessToken);
    }

}
