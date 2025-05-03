package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.domain.enums.model.NotificationCategory;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaNotificationRepository extends JpaRepository<JpaNotificationEntity, UUID> {

    @Query(
            "SELECT n FROM JpaNotificationEntity n " +
            "WHERE n.id = :id AND n.userId = :userId"
    )
    Optional<JpaNotificationEntity> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query(
            "SELECT n FROM JpaNotificationEntity n " +
            "WHERE n.userId = :userId AND n.createdAt > :startDate " +
            "ORDER BY n.createdAt DESC"
    )
    List<JpaNotificationEntity> findAllByUserIdInIntervalOrderByCreatedAtDesc(
            @Param("userId") UUID userId,
            @Param("startDate") Instant startDate
    );

    @Query(
            "SELECT n FROM JpaNotificationEntity n " +
            "WHERE n.userId = :userId AND n.category = :category AND n.createdAt > :startDate " +
            "ORDER BY n.createdAt DESC"
    )
    List<JpaNotificationEntity> findAllByUserIdAndCategoryInIntervalOrderByCreatedAtDesc(
            @Param("userId") UUID userId,
            @Param("category") String category,
            @Param("startDate") Instant startDate
    );

    @Modifying
    @Query("UPDATE JpaNotificationEntity n SET n.isRead = true WHERE n.id = :id")
    JpaNotificationEntity updateSetIsReadTrueById(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE JpaNotificationEntity n SET n.isRead = true WHERE n.id = :id AND n.userId = :userId")
    JpaNotificationEntity updateSetIsReadTrueByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE JpaNotificationEntity n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    List<JpaNotificationEntity> updateAllSetIsReadTrueByUserIdAndIsReadFalse(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM JpaNotificationEntity n WHERE n.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM JpaNotificationEntity n WHERE n.id = :id AND n.userId = :userId")
    void deleteAllByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query(
        "SELECT n FROM JpaNotificationEntity n " +
        "WHERE n.userId = :userId " +
        "ORDER BY n.createdAt DESC"
    )
    Page<JpaNotificationEntity> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query(
        "SELECT n FROM JpaNotificationEntity n " +
        "WHERE n.userId = :userId AND n.category = :category " +
        "ORDER BY n.createdAt DESC"
    )
    Page<JpaNotificationEntity> findAllByUserIdAndCategory(
            @Param("userId") UUID userId,
            @Param("category") NotificationCategory category,
            Pageable pageable
    );

}
