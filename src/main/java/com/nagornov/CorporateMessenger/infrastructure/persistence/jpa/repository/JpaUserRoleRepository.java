package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaRoleMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserRoleMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserRoleRepository {

    private final SpringDataJpaUserRoleRepository springDataJpaUserRoleRepository;
    private final JpaUserRoleMapper jpaUserRoleMapper;
    private final JpaUserMapper jpaUserMapper;
    private final JpaRoleMapper jpaRoleMapper;

    public List<Role> findRolesByUserId(UUID userId) {
        return springDataJpaUserRoleRepository
                .findRolesByUserId(userId)
                .stream().map(jpaRoleMapper::toDomain).toList();
    }

    public void assignRoleToUserId(UUID roleId, UUID userId) {
        springDataJpaUserRoleRepository.assignRoleToUserId(roleId, userId);
    }

}
