package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRegistrationKeyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaRegistrationKeyRepository extends JpaRepository<JpaRegistrationKeyEntity, UUID> {

    @Query("SELECT rk FROM JpaRegistrationKeyEntity rk WHERE rk.id = :id")
    Optional<JpaRegistrationKeyEntity> findById(@Param("id") UUID id);

    @Query("SELECT rk FROM JpaRegistrationKeyEntity rk WHERE rk.value = :value")
    Optional<JpaRegistrationKeyEntity> findByValue(@Param("value") String value);

    @Query("SELECT rk FROM JpaRegistrationKeyEntity rk ORDER BY rk.isApplied ASC")
    Page<JpaRegistrationKeyEntity> findAllSortedByNotApplied(Pageable pageable);

}