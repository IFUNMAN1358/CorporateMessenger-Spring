package com.nagornov.CorporateMessenger.domain.broker.producer;

import com.nagornov.CorporateMessenger.domain.enums.model.NotificationType;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaNotificationRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaNotificationDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaNotificationRepository kafkaNotificationRepository;

    private void sendToKafkaAsContactCategory(
            @NonNull UUID recipientUserId,
            @NonNull UUID relatedUserId,
            @NonNull NotificationType notificationType
    ) {
        KafkaNotificationDTO kafkaNotificationDTO = new KafkaNotificationDTO(
                recipientUserId,
                relatedUserId,
                null,
                notificationType
        );
        kafkaNotificationRepository.send(kafkaNotificationDTO);
    }

    public void sendToKafkaAsRequestToJoinContact(@NonNull UUID recipientUserId, @NonNull UUID relatedUserId) {
        sendToKafkaAsContactCategory(recipientUserId, relatedUserId, NotificationType.REQUEST_TO_JOIN_CONTACT);
    }

    public void sendToKafkaAsJoinContact(@NonNull UUID recipientUserId, @NonNull UUID relatedUserId) {
        sendToKafkaAsContactCategory(recipientUserId, relatedUserId, NotificationType.JOIN_CONTACT);
    }

    public void sendToKafkaAsConfirmContactRequest(@NonNull UUID recipientUserId, @NonNull UUID relatedUserId) {
        sendToKafkaAsContactCategory(recipientUserId, relatedUserId, NotificationType.CONFIRM_CONTACT_REQUEST);
    }

    public void sendToKafkaAsRejectContactRequest(@NonNull UUID recipientUserId, @NonNull UUID relatedUserId) {
        sendToKafkaAsContactCategory(recipientUserId, relatedUserId, NotificationType.REJECT_CONTACT_REQUEST);
    }

}
