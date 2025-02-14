package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.Log;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KafkaLogProducerRepository {

    private final KafkaTemplate<String, Log> kafkaLogTemplate;

    public void sendMessage(@NotNull Log message) {
        kafkaLogTemplate.send(
                KafkaTopic.LOG_TOPIC.getName(),
                message
        );
    }

}
