package com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.security.domain.model.SecuritySession;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.mapper.SecurityJpaSessionMapper;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.springData.SecuritySpringDataJpaSessionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SecurityJpaSessionRepository {

    private final SecuritySpringDataJpaSessionRepository springDataJpaSessionRepository;
    private final SecurityJpaSessionMapper jpaSessionMapper;

    public Optional<SecuritySession> findSessionById(@NotNull UUID id) {
        return springDataJpaSessionRepository.findSecurityJpaSessionEntityById(id)
                .map(jpaSessionMapper::toDomain);
    }

    public Boolean existsSessionByAccessToken(@NotNull String accessToken) {
        return springDataJpaSessionRepository
                .existsSecurityJpaSessionEntityByAccessToken(accessToken);
    }
}
