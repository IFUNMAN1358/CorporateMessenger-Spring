package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaSessionEntity;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.mapper.AuthJpaSessionMapper;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData.AuthSpringDataJpaSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuthJpaSessionRepository {

    private final AuthSpringDataJpaSessionRepository springDataJpaSessionRepository;
    private final AuthJpaSessionMapper jpaSessionMapper;

    public Optional<AuthSession> findSessionById(UUID id) {
        return springDataJpaSessionRepository
                .findAuthJpaSessionEntityById(id)
                .map(jpaSessionMapper::toDomain);
    }

    public Optional<AuthSession> findSessionByUserId(UUID userId) {
        return springDataJpaSessionRepository
                .findAuthJpaSessionEntityByUserId(userId)
                .map(jpaSessionMapper::toDomain);
    }

    public AuthSession save(AuthSession session) {
        AuthJpaSessionEntity entity = springDataJpaSessionRepository.save(
                jpaSessionMapper.toEntity(session)
        );
        return jpaSessionMapper.toDomain(entity);
    }

    public void delete(AuthSession session) {
        springDataJpaSessionRepository.delete(
                jpaSessionMapper.toEntity(session)
        );
    }
}
