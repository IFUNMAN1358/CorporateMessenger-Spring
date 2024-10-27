package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthSpringDataJpaSessionRepository extends JpaRepository<AuthJpaSessionEntity, UUID> {

    @Query("select s from AuthJpaSessionEntity s where s.id = :id")
    Optional<AuthJpaSessionEntity> findAuthJpaSessionEntityById(@Param("id") UUID id);

    @Query("select s from AuthJpaSessionEntity s where s.user.id = :userId")
    Optional<AuthJpaSessionEntity> findAuthJpaSessionEntityByUserId(@Param("userId") UUID userId);

}
