package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.Role;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaRoleMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaRoleRepository {

    private final SpringDataJpaRoleRepository springDataJpaRoleRepository;
    private final JpaRoleMapper jpaRoleMapper;

    public Optional<Role> findByName(String name) {
        return springDataJpaRoleRepository
                .findJpaRoleEntityByName(name)
                .map(jpaRoleMapper::toDomain);

    }
}
