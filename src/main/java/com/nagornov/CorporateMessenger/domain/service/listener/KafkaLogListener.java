package com.nagornov.CorporateMessenger.domain.service.listener;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaGroup;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.domain.service.domainService.externalApi.LogServiceClient;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.Log;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaLogListener {

    private final LogServiceClient logServiceClient;

    @KafkaListener(
            topics = KafkaTopic.LOG_TOPIC_NAME,
            groupId = KafkaGroup.LOG_GROUP_NAME,
            containerFactory = "kafkaLogContainerFactory"
    )
    public void distributor(ConsumerRecord<String, Log> record, Acknowledgment ack) {

        Log log = record.value();

        try {
            logServiceClient.sendLog(log);
            ack.acknowledge();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}