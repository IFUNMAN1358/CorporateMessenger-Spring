package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

    @Query("select u from JpaUserEntity u where u.id = :id")
    Optional<JpaUserEntity> findJpaUserEntityById(@Param("id") UUID id);

    @Query("select u from JpaUserEntity u where u.username = :username")
    Optional<JpaUserEntity> findJpaUserEntityByUsername(@Param("username") String username);

    @Query(
            "select u from JpaUserEntity u " +
            "where :username = '' or lower(u.username) like lower(concat('%', :username, '%'))"
    )
    List<JpaUserEntity> searchByUsername(@Param("username") String username, Pageable pageable);

    boolean existsByUsername(String username);

}
