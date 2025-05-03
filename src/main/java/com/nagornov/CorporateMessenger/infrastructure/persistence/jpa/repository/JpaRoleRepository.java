package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaRoleMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaRoleRepository {

    private final SpringDataJpaRoleRepository springDataJpaRoleRepository;
    private final JpaRoleMapper jpaRoleMapper;

    public Role save(Role role) {
        return jpaRoleMapper.toDomain(
                springDataJpaRoleRepository.save(
                        jpaRoleMapper.toEntity(role)
                )
        );
    }

    public void delete(Role role) {
        springDataJpaRoleRepository.delete(
                jpaRoleMapper.toEntity(role)
        );
    }

    public Optional<Role> findByName(RoleName name) {
        return springDataJpaRoleRepository
                .findByName(name)
                .map(jpaRoleMapper::toDomain);

    }

    public Optional<Role> findById(UUID id) {
        return springDataJpaRoleRepository
                .findById(id).
                map(jpaRoleMapper::toDomain);
    }

    public List<Role> findAllByUserId(UUID userId) {
        return springDataJpaRoleRepository
                .findAllByUserId(userId)
                .stream().map(jpaRoleMapper::toDomain).toList();
    }

    public Optional<Role> findByUserIdAndRoleId(UUID userId, UUID roleId) {
        return springDataJpaRoleRepository
                .findByUserIdAndRoleId(userId, roleId)
                .map(jpaRoleMapper::toDomain);
    }

    public Optional<Role> findByUserIdAndRoleName(UUID userId, RoleName name) {
        return springDataJpaRoleRepository
                .findByUserIdAndRoleName(userId, name)
                .map(jpaRoleMapper::toDomain);
    }
}
