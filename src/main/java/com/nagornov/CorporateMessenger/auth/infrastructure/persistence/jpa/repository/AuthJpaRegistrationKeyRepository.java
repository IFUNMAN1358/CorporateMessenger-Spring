package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthRegistrationKey;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaRegistrationKeyEntity;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.mapper.AuthJpaRegistrationKeyMapper;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData.AuthSpringDataJpaRegistrationKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthJpaRegistrationKeyRepository {

    private final AuthSpringDataJpaRegistrationKeyRepository springDataJpaRegistrationKeyRepository;
    private final AuthJpaRegistrationKeyMapper jpaRegistrationKeyMapper;

    public Optional<AuthRegistrationKey> findRegistrationKeyByValue(String key) {
        return springDataJpaRegistrationKeyRepository
                .findAuthJpaRegistrationKeyEntityByValue(key)
                .map(jpaRegistrationKeyMapper::toDomain);
    }

    public AuthRegistrationKey save(AuthRegistrationKey registrationKey) {
        AuthJpaRegistrationKeyEntity entity = springDataJpaRegistrationKeyRepository
                .save(jpaRegistrationKeyMapper.toEntity(registrationKey));
        return jpaRegistrationKeyMapper.toDomain(entity);
    }

}
