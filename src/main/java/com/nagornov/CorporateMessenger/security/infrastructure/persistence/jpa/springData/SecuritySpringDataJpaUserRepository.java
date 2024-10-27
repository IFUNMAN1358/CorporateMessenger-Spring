package com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.entity.SecurityJpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecuritySpringDataJpaUserRepository extends JpaRepository<SecurityJpaUserEntity, UUID> {

    Optional<SecurityJpaUserEntity> findSecurityJpaUserEntityById(UUID id);

}
