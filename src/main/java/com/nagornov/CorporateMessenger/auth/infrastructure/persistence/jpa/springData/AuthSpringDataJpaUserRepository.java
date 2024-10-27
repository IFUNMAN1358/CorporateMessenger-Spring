package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthSpringDataJpaUserRepository extends JpaRepository<AuthJpaUserEntity, UUID> {

    @Query("select u from AuthJpaUserEntity u where u.id = :id")
    Optional<AuthJpaUserEntity> findAuthJpaUserEntityById(@Param("id") UUID id);

    @Query("select u from AuthJpaUserEntity u where u.username = :username")
    Optional<AuthJpaUserEntity> findAuthJpaUserEntityByUsername(@Param("username") String username);

}
