package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.domain.model.user.UserRole;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaRoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final JpaRoleRepository jpaRoleRepository;
    private final UserRoleService userRoleService;

    @Transactional
    public Role create(@NonNull RoleName name) {
        Role role = new Role(
                UUID.randomUUID(),
                name,
                Instant.now()
        );
        return jpaRoleRepository.save(role);
    }

    public Optional<Role> findByName(@NonNull RoleName name) {
        return jpaRoleRepository.findByName(name);
    }

    public Role getByName(@NonNull RoleName name) {
        return jpaRoleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role[name=%s] not found".formatted(name)));
    }

    public List<Role> findAllByUserId(@NonNull UUID userId) {
        return jpaRoleRepository.findAllByUserId(userId);
    }

    public Optional<Role> findByUserIdAndRoleId(@NonNull UUID userId, @NonNull UUID roleId) {
        return jpaRoleRepository.findByUserIdAndRoleId(userId, roleId);
    }

    public Optional<Role> findByUserIdAndRoleName(@NonNull UUID userId, @NonNull RoleName name) {
        return jpaRoleRepository.findByUserIdAndRoleName(userId, name);
    }

    @Transactional
    public Role assignRoleToUserId(@NonNull UUID userId, @NonNull RoleName name) {
        Role role = getByName(name);
        userRoleService.create(userId, role.getId());
        return role;
    }

    @Transactional
    public void removeRoleFromUserId(@NonNull UUID userId, @NonNull RoleName name) {
        UserRole optRole = userRoleService.getByUserIdAndRoleName(userId, name);
        userRoleService.delete(optRole);
    }

}
