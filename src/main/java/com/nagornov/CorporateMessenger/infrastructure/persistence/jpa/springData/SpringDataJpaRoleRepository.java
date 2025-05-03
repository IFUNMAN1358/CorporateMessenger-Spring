package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaRoleRepository extends JpaRepository<JpaRoleEntity, UUID> {

    @Query("select r from JpaRoleEntity r where r.name = :name")
    Optional<JpaRoleEntity> findByName(@Param("name") RoleName name);

    @Query(
            "SELECT r FROM JpaRoleEntity r " +
            "JOIN JpaUserRoleEntity ur ON r.id = ur.roleId " +
            "WHERE ur.userId = :userId"
    )
    List<JpaRoleEntity> findAllByUserId(@Param("userId") UUID userId);

    @Query(
            "SELECT r FROM JpaRoleEntity r " +
            "JOIN JpaUserRoleEntity ur ON r.id = ur.roleId " +
            "WHERE ur.userId = :userId AND ur.roleId = :roleId"
    )
    Optional<JpaRoleEntity> findByUserIdAndRoleId(@Param("userId") UUID userId, @Param("roleId") UUID roleId);

    @Query(
            "SELECT r FROM JpaRoleEntity r " +
            "JOIN JpaUserRoleEntity ur ON r.id = ur.roleId " +
            "WHERE ur.userId = :userId AND r.name = :name"
    )
    Optional<JpaRoleEntity> findByUserIdAndRoleName(@Param("userId") UUID userId, @Param("name") RoleName name);

}
