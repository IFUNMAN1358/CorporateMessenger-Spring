package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaUserSettingsRepository extends JpaRepository<JpaUserSettingsEntity, UUID> {

    @Query("SELECT us FROM JpaUserSettingsEntity us WHERE us.userId = :userId")
    Optional<JpaUserSettingsEntity> findByUserId(@Param("userId") UUID userId);

}
