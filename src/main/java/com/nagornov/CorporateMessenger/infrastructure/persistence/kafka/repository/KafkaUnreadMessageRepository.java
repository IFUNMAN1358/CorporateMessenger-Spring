package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KafkaUnreadMessageRepository {

    private final KafkaTemplate<String, KafkaUnreadMessageDTO> kafkaUnreadMessageTemplate;

    public void sendMessage(KafkaUnreadMessageDTO message) {
        kafkaUnreadMessageTemplate.send(
                KafkaTopic.UNREAD_MESSAGE_TOPIC.getName(),
                message
        );
    }

}
