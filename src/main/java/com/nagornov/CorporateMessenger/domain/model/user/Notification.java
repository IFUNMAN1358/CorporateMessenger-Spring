package com.nagornov.CorporateMessenger.domain.model.user;

import com.nagornov.CorporateMessenger.domain.enums.model.NotificationCategory;
import com.nagornov.CorporateMessenger.domain.enums.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Notification {

    private UUID id;
    private UUID userId;
    private NotificationCategory category;
    private NotificationType type;
    private Boolean isRead;
    private Boolean isProcessed;
    private String text;
    private Map<String, Object> payload;
    private Instant createdAt;

    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsProcessed() {
        this.isProcessed = true;
    }

}
