package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRoleEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataJpaUserRoleRepository extends JpaRepository<JpaUserRoleEntity, UUID> {

    @Query(
            value = "SELECT r.* FROM roles r " +
                    "INNER JOIN user_role ur ON r.id = ur.role_id " +
                    "WHERE ur.user_id = :userId",
            nativeQuery = true
    )
    List<JpaRoleEntity> findRolesByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query(
            value = "INSERT INTO user_role(id, userId, roleId) VALUES (uuid_generate_v4(), :userId, :roleId)",
            nativeQuery = true
    )
    void assignRoleToUserId(@Param("roleId") UUID roleId, @Param("userId") UUID userId);

}
