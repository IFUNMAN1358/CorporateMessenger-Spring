package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository;

import com.nagornov.CorporateMessenger.domain.enums.KafkaTopic;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KafkaUnreadMessageProducerRepository {

    private final KafkaTemplate<String, KafkaUnreadMessageDTO> kafkaUnreadMessageTemplate;

    public void sendMessage(@NotNull KafkaUnreadMessageDTO message) {
        kafkaUnreadMessageTemplate.send(
                KafkaTopic.UNREAD_MESSAGE_TOPIC.getName(),
                message
        );
    }

}
