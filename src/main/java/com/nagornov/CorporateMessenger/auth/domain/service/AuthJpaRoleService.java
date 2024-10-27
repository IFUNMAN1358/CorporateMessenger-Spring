package com.nagornov.CorporateMessenger.auth.domain.service;

import com.nagornov.CorporateMessenger.auth.domain.enums.RoleEnum;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthRole;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.repository.AuthJpaRoleRepository;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa.JpaRoleNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthJpaRoleService {

    private final AuthJpaRoleRepository jpaRoleRepository;

    public AuthRole getRoleByName(@NotNull RoleEnum enumRole) {
        return jpaRoleRepository.findRoleByName(enumRole.getName())
                .orElseThrow(JpaRoleNotFoundException::new);
    }
}
