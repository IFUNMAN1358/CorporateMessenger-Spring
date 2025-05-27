package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserBlacklistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaUserBlacklistRepository extends JpaRepository<JpaUserBlacklistEntity, UUID> {

    @Query("SELECT ub FROM JpaUserBlacklistEntity ub WHERE ub.userId = :userId")
    List<JpaUserBlacklistEntity> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT ub FROM JpaUserBlacklistEntity ub WHERE ub.userId = :userId")
    Page<JpaUserBlacklistEntity> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT ub FROM JpaUserBlacklistEntity ub WHERE ub.userId = :userId AND ub.blockedUserId = :blockedUserId")
    Optional<JpaUserBlacklistEntity> findByUserIdAndBlockedUserId(
            @Param("userId") UUID userId,
            @Param("blockedUserId") UUID blockedUserId
    );

    @Query("SELECT COUNT(ub) > 0 FROM JpaUserBlacklistEntity ub WHERE ub.userId = :userId AND ub.blockedUserId = :blockedUserId")
    boolean existsByUserIdAndBlockedUserId(@Param("userId") UUID userId, @Param("blockedUserId") UUID blockedUserId);

    @Query(
            "SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END " +
            "FROM JpaUserBlacklistEntity ub " +
            "WHERE (ub.userId = :userId1 AND ub.blockedUserId = :userId2) OR (ub.userId = :userId2 AND ub.blockedUserId = :userId1)"
    )
    boolean existsAnyBetweenUserIds(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    @Query(
            "SELECT CASE WHEN " +
            "EXISTS (SELECT COUNT(ub1) > 0 FROM JpaUserBlacklistEntity ub1 WHERE ub1.userId = :userId1 AND ub1.blockedUserId = :userId2) AND " +
            "EXISTS (SELECT COUNT(ub2) > 0 FROM JpaUserBlacklistEntity ub2 WHERE ub2.userId = :userId2 AND ub2.blockedUserId = :userId1) " +
            "THEN true ELSE false END"
    )
    boolean existsAllBetweenUserIds(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    @Modifying
    @Query("DELETE FROM JpaUserBlacklistEntity ub WHERE ub.userId = :userId AND ub.blockedUserId = :blockedUserId")
    void deleteByUserIdAndBlockedUserId(@Param("userId") UUID userId, @Param("blockedUserId") UUID blockedUserId);

}
