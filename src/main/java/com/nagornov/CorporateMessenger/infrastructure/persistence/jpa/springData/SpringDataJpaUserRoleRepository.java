package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.domain.enums.model.RoleName;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaUserRoleRepository extends JpaRepository<JpaUserRoleEntity, UUID> {

    @Query(
            "SELECT ur FROM JpaUserRoleEntity ur " +
            "JOIN JpaRoleEntity r ON ur.roleId = r.id " +
            "WHERE ur.userId = :userId AND r.name = :name"
    )
    Optional<JpaUserRoleEntity> findByUserIdAndRoleName(@Param("userId") UUID userId, @Param("name") RoleName name);

}
