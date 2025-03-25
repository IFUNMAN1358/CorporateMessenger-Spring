package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRoleEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaRoleMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaRoleRepository {

    private final SpringDataJpaRoleRepository springDataJpaRoleRepository;
    private final JpaRoleMapper jpaRoleMapper;

    public Role save(Role role) {
        JpaRoleEntity entity = springDataJpaRoleRepository.save(
                jpaRoleMapper.toEntity(role)
        );
        return jpaRoleMapper.toDomain(entity);
    }

    public void delete(Role role) {
        springDataJpaRoleRepository.delete(
                jpaRoleMapper.toEntity(role)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaRoleRepository.deleteById(id);
    }

    public Optional<Role> findByName(String name) {
        return springDataJpaRoleRepository
                .findJpaRoleEntityByName(name)
                .map(jpaRoleMapper::toDomain);

    }

    public Optional<Role> findById(UUID id) {
        return springDataJpaRoleRepository
                .findById(id).
                map(jpaRoleMapper::toDomain);
    }
}
