package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository;

import com.nagornov.CorporateMessenger.domain.enums.KafkaTopic;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KafkaTestProducerRepository {

    private final KafkaTemplate<String, String> kafkaTestTemplate;

    public void sendMessage(@NotNull String message) {
        kafkaTestTemplate.send(
                KafkaTopic.TEST_TOPIC.getName(),
                message
        );
    }

}
