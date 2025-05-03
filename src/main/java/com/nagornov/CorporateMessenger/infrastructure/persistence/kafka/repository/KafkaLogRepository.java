package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.domain.model.log.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KafkaLogRepository {

    private final KafkaTemplate<String, Log> kafkaLogTemplate;

    public void sendMessage(Log message) {
        kafkaLogTemplate.send(
                KafkaTopic.LOG_TOPIC.getName(),
                message
        );
    }

}
