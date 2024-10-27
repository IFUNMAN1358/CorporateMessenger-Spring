package com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.entity.SecurityJpaSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecuritySpringDataJpaSessionRepository extends JpaRepository<SecurityJpaSessionEntity, UUID> {

    Optional<SecurityJpaSessionEntity> findSecurityJpaSessionEntityById(UUID id);

    Boolean existsSecurityJpaSessionEntityByAccessToken(String accessToken);

}
