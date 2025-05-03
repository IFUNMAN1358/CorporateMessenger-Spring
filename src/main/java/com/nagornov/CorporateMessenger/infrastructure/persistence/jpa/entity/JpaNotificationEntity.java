package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity;

import com.nagornov.CorporateMessenger.domain.enums.model.NotificationCategory;
import com.nagornov.CorporateMessenger.domain.enums.model.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaNotificationEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "category", length = 32, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    @Column(name = "type", length = 64, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "is_processed", nullable = false)
    private Boolean isProcessed;

    @Column(name = "text", updatable = false)
    private String text;

    @Column(name = "payload", updatable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
