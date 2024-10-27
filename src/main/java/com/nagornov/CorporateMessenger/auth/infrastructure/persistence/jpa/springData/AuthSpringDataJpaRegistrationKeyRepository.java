package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaRegistrationKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthSpringDataJpaRegistrationKeyRepository extends JpaRepository<AuthJpaRegistrationKeyEntity, UUID> {

    Optional<AuthJpaRegistrationKeyEntity> findAuthJpaRegistrationKeyEntityByValue(String value);

}
