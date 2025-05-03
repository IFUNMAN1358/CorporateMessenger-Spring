package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto;

import com.nagornov.CorporateMessenger.domain.enums.model.NotificationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KafkaNotificationDTO {

    private UUID recipientUserId;
    private UUID relatedUserId;
    private Long relatedChatId;
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

}
