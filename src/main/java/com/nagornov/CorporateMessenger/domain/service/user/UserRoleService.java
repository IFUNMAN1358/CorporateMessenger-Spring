package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.UserRole;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserRoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final JpaUserRoleRepository jpaUserRoleRepository;

    @Transactional
    public UserRole create(@NonNull UUID userId, @NonNull UUID roleId) {
        UserRole userRole = new UserRole(
                UUID.randomUUID(),
                userId,
                roleId
        );
        return jpaUserRoleRepository.save(userRole);
    }

    @Transactional
    public void delete(@NonNull UserRole userRole) {
        jpaUserRoleRepository.delete(userRole);
    }

    public Optional<UserRole> findByUserIdAndRoleName(@NonNull UUID userId, @NonNull RoleName name) {
        return jpaUserRoleRepository.findByUserIdAndRoleName(userId, name);
    }

    public UserRole getByUserIdAndRoleName(@NonNull UUID userId, @NonNull RoleName name) {
        return jpaUserRoleRepository.findByUserIdAndRoleName(userId, name)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole[userId=%s] by Role[name=%s] not found".formatted(userId, name)));
    }

}
