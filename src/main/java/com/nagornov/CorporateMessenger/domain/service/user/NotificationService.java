package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaGroup;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.domain.enums.model.NotificationCategory;
import com.nagornov.CorporateMessenger.domain.enums.model.NotificationType;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.Notification;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaNotificationRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaNotificationRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaNotificationDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JpaNotificationRepository jpaNotificationRepository;
    private final KafkaNotificationRepository kafkaNotificationRepository;
    private final UserService userService;
    private final UserPhotoService UserPhotoService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    //
    //
    //

    public NotificationCategory defineCategoryFromString(@NonNull String stringCategory) {
        return switch (stringCategory) {
            case "all" -> NotificationCategory.ALL;
            case "account" -> NotificationCategory.ACCOUNT;
            case "chat" -> NotificationCategory.CHAT;
            case "contact" -> NotificationCategory.CONTACT;
            default -> throw new ResourceBadRequestException("Illegal category in request param");
        };
    }

    //
    // JPA
    //

    public Notification update(@NonNull Notification notification) {
        return jpaNotificationRepository.save(notification);
    }

    public void readAllByUserId(@NonNull UUID userId) {
        jpaNotificationRepository.updateAllSetIsReadTrueByUserIdAndIsReadFalse(userId);
    }

    public Page<Notification> findAllByUserId(@NonNull UUID userId, int page, int pageSize) {
        return jpaNotificationRepository.findAllByUserId(userId, page, pageSize);
    }

    public Page<Notification> findAllByUserIdAndCategory(@NonNull UUID userId, @NonNull NotificationCategory category, int page, int pageSize) {
        return jpaNotificationRepository.findAllByUserIdAndCategory(userId, category, page, pageSize);
    }

    public Notification getByIdAndUserId(@NonNull UUID id, @NonNull UUID userId) {
        return jpaNotificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification[id=%s, userId=%s] not found".formatted(id, userId)));
    }

    public void deleteByIdAndUserId(@NonNull UUID id, @NonNull UUID userId) {
        jpaNotificationRepository.deleteByIdAndUserId(id, userId);
    }

    public void deleteAllByUserId(@NonNull UUID userId) {
        jpaNotificationRepository.deleteAllByUserId(userId);
    }

    private void createAsContactCategory(
            @NonNull UUID recipientUserId,
            @NonNull UUID relatedUserId,
            @NonNull NotificationType notificationType
    ) {
        User relatedUser = userService.getById(relatedUserId);
        Optional<UserPhoto> relatedUserPhoto = UserPhotoService.findMainByUserId(relatedUser.getId());

        Map<String, Object> payload = new HashMap<>();
        payload.put("relatedType", "user");
        payload.put("relatedUserId", relatedUser.getId());
        payload.put("relatedUserUsername", relatedUser.getUsername());
        payload.put("relatedUserFirstName", relatedUser.getFirstName());
        payload.put("relatedUserLastName", relatedUser.getLastName());
        payload.put("relatedUserPhotoId", relatedUserPhoto.isPresent() ? relatedUserPhoto.get().getId() : null);

        Notification notification = new Notification(
                UUID.randomUUID(),
                recipientUserId,
                NotificationCategory.CONTACT,
                notificationType,
                false,
                false,
                null,
                payload,
                Instant.now()
        );
        jpaNotificationRepository.save(notification);
        simpMessagingTemplate.convertAndSendToUser(recipientUserId.toString(), "/queue/notifications", notification);
    }

    private void createAsRequestToJoinContact(@NonNull UUID recipientUserId, @NonNull UUID relatedUserId) {
        createAsContactCategory(recipientUserId, relatedUserId, NotificationType.REQUEST_TO_JOIN_CONTACT);
    }

    private void createAsJoinContact(@NonNull UUID recipientUserId, @NonNull UUID relatedUserId) {
        createAsContactCategory(recipientUserId, relatedUserId, NotificationType.JOIN_CONTACT);
    }

    private void createAsConfirmContactRequest(@NonNull UUID recipientUserId, @NonNull UUID relatedUserId) {
        createAsContactCategory(recipientUserId, relatedUserId, NotificationType.CONFIRM_CONTACT_REQUEST);
    }

    private void createAsRejectContactRequest(@NonNull UUID recipientUserId, @NonNull UUID relatedUserId) {
        createAsContactCategory(recipientUserId, relatedUserId, NotificationType.REJECT_CONTACT_REQUEST);
    }

    //
    // KAFKA
    //

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
                    createAsJoinContact(dto.getRecipientUserId(), dto.getRelatedUserId());
                    break;
                case NotificationType.REQUEST_TO_JOIN_CONTACT:
                    createAsRequestToJoinContact(dto.getRecipientUserId(), dto.getRelatedUserId());
                    break;
                case NotificationType.CONFIRM_CONTACT_REQUEST:
                    createAsConfirmContactRequest(dto.getRecipientUserId(), dto.getRelatedUserId());
                    break;
                case NotificationType.REJECT_CONTACT_REQUEST:
                    createAsRejectContactRequest(dto.getRecipientUserId(), dto.getRelatedUserId());
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
