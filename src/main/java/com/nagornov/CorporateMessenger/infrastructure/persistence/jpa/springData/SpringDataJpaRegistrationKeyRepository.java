package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRegistrationKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaRegistrationKeyRepository extends JpaRepository<JpaRegistrationKeyEntity, UUID> {

    Optional<JpaRegistrationKeyEntity> findJpaRegistrationKeyEntityByValue(String value);

}
