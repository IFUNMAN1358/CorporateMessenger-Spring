package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaRoleRepository extends JpaRepository<JpaRoleEntity, UUID> {

    @Query("select r from JpaRoleEntity r where r.name = :name")
    Optional<JpaRoleEntity> findByName(@Param("name") String name);

}
