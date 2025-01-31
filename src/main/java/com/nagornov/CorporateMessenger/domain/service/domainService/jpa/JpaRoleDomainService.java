package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.enums.RoleEnum;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.Role;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaRoleRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaRoleDomainService {

    private final JpaRoleRepository jpaRoleRepository;

    public Role getByName(@NotNull RoleEnum enumRole) {
        return jpaRoleRepository.findByName(enumRole.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Role with this name not found"));
    }
}
