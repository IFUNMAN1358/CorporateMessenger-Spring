package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaUserEntity;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.mapper.AuthJpaUserMapper;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData.AuthSpringDataJpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuthJpaUserRepository {

    private final AuthSpringDataJpaUserRepository springDataJpaUserRepository;
    private final AuthJpaUserMapper jpaUserMapper;

    public Optional<AuthUser> findUserById(UUID id) {
        return springDataJpaUserRepository
                .findAuthJpaUserEntityById(id)
                .map(jpaUserMapper::toDomain);
    }

    public Optional<AuthUser> findUserByUsername(String username) {
        return springDataJpaUserRepository
                .findAuthJpaUserEntityByUsername(username)
                .map(jpaUserMapper::toDomain);
    }

    public AuthUser save(AuthUser user) {
        AuthJpaUserEntity entity = springDataJpaUserRepository.save(
                jpaUserMapper.toEntity(user)
        );
        return jpaUserMapper.toDomain(entity);
    }

    public void delete(AuthUser user) {
        springDataJpaUserRepository.delete(
                jpaUserMapper.toEntity(user)
        );
    }
}
