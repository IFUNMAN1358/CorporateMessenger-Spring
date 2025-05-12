package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaExternalServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaExternalServiceRepository extends JpaRepository<JpaExternalServiceEntity, UUID> {

    @Query("SELECT ex FROM JpaExternalServiceEntity ex WHERE ex.name = :name")
    Optional<JpaExternalServiceEntity> findByName(@Param("name") String name);

    @Query("SELECT ex FROM JpaExternalServiceEntity ex WHERE ex.name = :name AND ex.apiKey = :apiKey")
    Optional<JpaExternalServiceEntity> findByNameAndApiKey(@Param("name") String name, @Param("apiKey") String apiKey);

}
