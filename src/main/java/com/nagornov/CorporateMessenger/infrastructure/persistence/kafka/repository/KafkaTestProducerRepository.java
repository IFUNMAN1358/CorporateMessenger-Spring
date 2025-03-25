package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KafkaTestProducerRepository {

    private final KafkaTemplate<String, String> kafkaTestTemplate;

    public void sendMessage(String message) {
        kafkaTestTemplate.send(
                KafkaTopic.TEST_TOPIC.getName(),
                message
        );
    }

}
