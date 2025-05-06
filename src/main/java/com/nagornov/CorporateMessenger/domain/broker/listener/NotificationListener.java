package com.nagornov.CorporateMessenger.domain.broker.listener;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaGroup;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.domain.enums.model.NotificationType;
import com.nagornov.CorporateMessenger.domain.service.user.NotificationService;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = KafkaTopic.NOTIFICATION_TOPIC_NAME,
        groupId = KafkaGroup.NOTIFICATION_GROUP_NAME,
        containerFactory = "kafkaNotificationContainerFactory",
        concurrency = "4"
    )
    public void kafkaNotificationListener(ConsumerRecord<String, KafkaNotificationDTO> record, Acknowledgment ack) {
        KafkaNotificationDTO dto = record.value();

        try {
            switch (dto.getNotificationType()) {
                case NotificationType.JOIN_CONTACT:
                    notificationService.createAsJoinContact(dto.getRecipientUserId(), dto.getRelatedUserId());
                    break;
                case NotificationType.REQUEST_TO_JOIN_CONTACT:
                    notificationService.createAsRequestToJoinContact(dto.getRecipientUserId(), dto.getRelatedUserId());
                    break;
                case NotificationType.CONFIRM_CONTACT_REQUEST:
                    notificationService.createAsConfirmContactRequest(dto.getRecipientUserId(), dto.getRelatedUserId());
                    break;
                case NotificationType.REJECT_CONTACT_REQUEST:
                    notificationService.createAsRejectContactRequest(dto.getRecipientUserId(), dto.getRelatedUserId());
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unknown NotificationType[%s] for KafkaNotificationDTO object".formatted(dto.getNotificationType())
                    );
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process kafka notification: {}", dto, e);
        }
    }
}