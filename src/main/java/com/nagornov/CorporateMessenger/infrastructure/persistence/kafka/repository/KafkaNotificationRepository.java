package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KafkaNotificationRepository {

    private final KafkaTemplate<String, KafkaNotificationDTO> kafkaNotificationTemplate;

    public void send(KafkaNotificationDTO kafkaNotificationDTO) {
        kafkaNotificationTemplate.send(
                KafkaTopic.NOTIFICATION_TOPIC_NAME,
                kafkaNotificationDTO
        );
    }

}
