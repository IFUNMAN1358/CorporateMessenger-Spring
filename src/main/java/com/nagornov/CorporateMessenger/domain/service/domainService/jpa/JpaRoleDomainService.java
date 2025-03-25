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

import java.util.UUID;

@Service
@CacheConfig(cacheNames = "roles")
@RequiredArgsConstructor
public class JpaRoleDomainService {

    private final JpaRoleRepository jpaRoleRepository;

    @CachePut(key = "#result.id")
    public Role save(@NonNull Role role) {
        return jpaRoleRepository.save(role);
    }

    @CacheEvict(key = "#role.id")
    public void delete(@NonNull Role role) {
        jpaRoleRepository.delete(role);
    }

    @CacheEvict(key = "#id")
    public void deleteById(@NonNull UUID id) {
        jpaRoleRepository.deleteById(id);
    }

    @Cacheable(key = "#result.id")
    public Role getByName(@NonNull RoleEnum enumRole) {
        return jpaRoleRepository.findByName(enumRole.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role with this name not found: %s".formatted(enumRole.getName()))
                );
    }
}
