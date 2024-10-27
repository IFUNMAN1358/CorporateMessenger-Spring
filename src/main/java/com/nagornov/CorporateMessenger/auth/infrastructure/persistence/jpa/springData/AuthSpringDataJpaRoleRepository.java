package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthSpringDataJpaRoleRepository extends JpaRepository<AuthJpaRoleEntity, UUID> {

    @Query("select r from AuthJpaRoleEntity r where r.name = :name")
    Optional<AuthJpaRoleEntity> findAuthJpaRoleEntityByName(@Param("name") String name);

}
