package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeePhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaEmployeePhotoRepository extends JpaRepository<JpaEmployeePhotoEntity, UUID> {

    @Query("SELECT ep FROM JpaEmployeePhotoEntity ep WHERE ep.employeeId = :employeeId")
    Optional<JpaEmployeePhotoEntity> findByEmployeeId(@Param("employeeId") UUID employeeId);

    @Modifying
    @Query("DELETE FROM JpaEmployeePhotoEntity ep WHERE ep.employeeId = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") UUID employeeId);

}
