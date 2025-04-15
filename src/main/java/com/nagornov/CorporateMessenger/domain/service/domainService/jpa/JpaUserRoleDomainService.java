package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.user.UserRole;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserRoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaUserRoleDomainService {

    private final JpaUserRoleRepository jpaUserRoleRepository;

    public UserRole save(@NonNull UserRole userRole) {
        return jpaUserRoleRepository.save(userRole);
    }

    public List<Role> findRolesByUserId(@NonNull UUID userId) {
        return jpaUserRoleRepository.findRolesByUserId(userId);
    }

    @Transactional
    public void assignRoleToUser(@NonNull UUID roleId, @NonNull UUID userId) {
        UserRole userRole = new UserRole(
                UUID.randomUUID(),
                userId,
                roleId
        );
        save(userRole);
    }

}
