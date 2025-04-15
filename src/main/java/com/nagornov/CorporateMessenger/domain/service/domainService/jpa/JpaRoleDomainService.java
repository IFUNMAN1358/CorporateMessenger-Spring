package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.enums.RoleEnum;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.Role;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaRoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "roles")
@RequiredArgsConstructor
public class JpaRoleDomainService {

    private final JpaRoleRepository jpaRoleRepository;

    @CachePut(key = "#role.name")
    public Role save(@NonNull Role role) {
        return jpaRoleRepository.save(role);
    }

    @CacheEvict(key = "#role.name")
    public void delete(@NonNull Role role) {
        jpaRoleRepository.delete(role);
    }

    @Cacheable(key = "#roleEnum.name()")
    public Role getByName(@NonNull RoleEnum roleEnum) {
        return jpaRoleRepository.findByName(roleEnum.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Role[name=%s] not found".formatted(roleEnum.getName())));
    }
}
