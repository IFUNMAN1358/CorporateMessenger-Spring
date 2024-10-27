package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthRole;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.mapper.AuthJpaRoleMapper;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData.AuthSpringDataJpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthJpaRoleRepository {

    private final AuthSpringDataJpaRoleRepository springDataJpaRoleRepository;
    private final AuthJpaRoleMapper jpaRoleMapper;

    public Optional<AuthRole> findRoleByName(String name) {
        return springDataJpaRoleRepository
                .findAuthJpaRoleEntityByName(name)
                .map(jpaRoleMapper::toDomain);

    }
}
