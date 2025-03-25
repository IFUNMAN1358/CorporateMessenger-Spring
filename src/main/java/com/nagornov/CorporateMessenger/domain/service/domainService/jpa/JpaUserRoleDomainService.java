package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserRoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaUserRoleDomainService {

    private final JpaUserRoleRepository jpaUserRoleRepository;

    public List<Role> findRolesByUserId(@NonNull UUID userId) {
        return jpaUserRoleRepository.findRolesByUserId(userId);
    }

    public void assignRoleIdToUserId(@NonNull UUID roleId, @NonNull UUID userId) {
        jpaUserRoleRepository.assignRoleToUserId(roleId, userId);
    }

}
