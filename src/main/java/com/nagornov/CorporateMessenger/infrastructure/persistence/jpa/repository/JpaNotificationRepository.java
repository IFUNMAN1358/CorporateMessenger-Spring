package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.enums.model.NotificationCategory;
import com.nagornov.CorporateMessenger.domain.model.user.Notification;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaNotificationMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaNotificationRepository {

    private final SpringDataJpaNotificationRepository springDataJpaNotificationRepository;
    private final JpaNotificationMapper jpaNotificationMapper;

    public Notification save(Notification notification) {
        return jpaNotificationMapper.toDomain(
                springDataJpaNotificationRepository.save(
                        jpaNotificationMapper.toEntity(notification)
                )
        );
    }

    public void delete(Notification notification) {
        springDataJpaNotificationRepository.delete(
                jpaNotificationMapper.toEntity(notification)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaNotificationRepository.deleteById(id);
    }

    public void deleteAllByUserId(UUID userId) {
        springDataJpaNotificationRepository.deleteAllByUserId(userId);
    }

    public void deleteByIdAndUserId(UUID id, UUID userId) {
        springDataJpaNotificationRepository.deleteAllByIdAndUserId(id, userId);
    }

    public Optional<Notification> findById(UUID id) {
        return springDataJpaNotificationRepository.findById(id).map(jpaNotificationMapper::toDomain);
    }

    public Optional<Notification> findByIdAndUserId(UUID id, UUID userId) {
        return springDataJpaNotificationRepository.findByIdAndUserId(id, userId)
                .map(jpaNotificationMapper::toDomain);
    }

    public Page<Notification> findAllByUserId(UUID userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return springDataJpaNotificationRepository.findAllByUserId(userId, pageable)
                .map(jpaNotificationMapper::toDomain);
    }

    public Page<Notification> findAllByUserIdAndCategory(UUID userId, NotificationCategory category, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return springDataJpaNotificationRepository.findAllByUserIdAndCategory(userId, category, pageable)
                .map(jpaNotificationMapper::toDomain);
    }

    public Notification updateSetIsReadTrueById(UUID id) {
        return jpaNotificationMapper.toDomain(
                springDataJpaNotificationRepository.updateSetIsReadTrueById(id)
        );
    }

    public Notification updateSetIsReadTrueByIdAndUserId(UUID id, UUID userId) {
        return jpaNotificationMapper.toDomain(
                springDataJpaNotificationRepository.updateSetIsReadTrueByIdAndUserId(id, userId)
        );
    }

    public List<Notification> updateAllSetIsReadTrueByUserIdAndIsReadFalse(UUID userId) {
        return springDataJpaNotificationRepository
                .updateAllSetIsReadTrueByUserIdAndIsReadFalse(userId)
                .stream().map(jpaNotificationMapper::toDomain).toList();
    }

    public List<Notification> findAllByUserIdInIntervalOrderByCreatedAtDesc(
            UUID userId,
            int daysInterval
    ) {
        Instant startDate = Instant.now().minus(Duration.ofDays(daysInterval));
        return springDataJpaNotificationRepository
                .findAllByUserIdInIntervalOrderByCreatedAtDesc(userId, startDate)
                .stream().map(jpaNotificationMapper::toDomain).toList();
    }

    public List<Notification> findAllByUserIdAndCategoryInIntervalOrderByCreatedAtDesc(
            UUID userId,
            String category,
            int daysInterval
    ) {
        Instant startDate = Instant.now().minus(Duration.ofDays(daysInterval));
        return springDataJpaNotificationRepository
                .findAllByUserIdAndCategoryInIntervalOrderByCreatedAtDesc(userId, category, startDate)
                .stream().map(jpaNotificationMapper::toDomain).toList();
    }

}
