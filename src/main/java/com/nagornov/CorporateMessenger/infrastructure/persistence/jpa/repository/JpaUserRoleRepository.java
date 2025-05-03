package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.domain.model.user.UserRole;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserRoleEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserRoleMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserRoleRepository {

    private final SpringDataJpaUserRoleRepository springDataJpaUserRoleRepository;
    private final JpaUserRoleMapper jpaUserRoleMapper;

    public UserRole save(UserRole userRole) {
        JpaUserRoleEntity entity = springDataJpaUserRoleRepository.save(
                jpaUserRoleMapper.toEntity(userRole)
        );
        return jpaUserRoleMapper.toDomain(entity);
    }

    public void delete(UserRole userRole) {
        springDataJpaUserRoleRepository.delete(
                jpaUserRoleMapper.toEntity(userRole)
        );
    }

    public Optional<UserRole> findByUserIdAndRoleName(UUID userId, RoleName name) {
        return springDataJpaUserRoleRepository
                .findByUserIdAndRoleName(userId, name)
                .map(jpaUserRoleMapper::toDomain);
    }

}