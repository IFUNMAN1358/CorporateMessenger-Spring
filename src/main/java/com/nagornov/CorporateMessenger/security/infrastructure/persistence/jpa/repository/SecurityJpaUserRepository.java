package com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.entity.SecurityJpaUserEntity;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.mapper.SecurityJpaUserMapper;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.springData.SecuritySpringDataJpaUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SecurityJpaUserRepository {

    private final SecuritySpringDataJpaUserRepository springDataJpaUserRepository;
    private final SecurityJpaUserMapper jpaUserMapper;

    public Optional<SecurityUser> findUserById(@NotNull UUID id) {
        return springDataJpaUserRepository.findSecurityJpaUserEntityById(id)
                .map(jpaUserMapper::toDomain);
    }

}
