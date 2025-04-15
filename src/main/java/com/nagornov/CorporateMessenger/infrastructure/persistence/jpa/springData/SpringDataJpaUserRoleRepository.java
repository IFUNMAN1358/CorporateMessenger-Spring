package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaRoleEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataJpaUserRoleRepository extends JpaRepository<JpaUserRoleEntity, UUID> {

    @Query(
            value = "SELECT r FROM JpaRoleEntity r " +
                    "INNER JOIN JpaUserRoleEntity ur ON r.id = ur.roleId " +
                    "WHERE ur.userId = :userId"
    )
    List<JpaRoleEntity> findRolesByUserId(@Param("userId") UUID userId);

}
