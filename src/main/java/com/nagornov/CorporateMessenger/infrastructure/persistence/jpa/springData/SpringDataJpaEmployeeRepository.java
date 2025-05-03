package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaEmployeeRepository extends JpaRepository<JpaEmployeeEntity, UUID> {

    @Modifying
    @Query("DELETE FROM JpaEmployeeEntity e WHERE e.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    @Query("SELECT e FROM JpaEmployeeEntity e WHERE e.userId = :userId")
    Optional<JpaEmployeeEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT e FROM JpaEmployeeEntity e WHERE e.leaderId = :leaderId")
    List<JpaEmployeeEntity> findAllByLeaderId(@Param("leaderId") UUID leaderId);

}
