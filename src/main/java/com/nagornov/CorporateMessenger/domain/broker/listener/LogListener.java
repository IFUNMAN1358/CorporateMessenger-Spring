package com.nagornov.CorporateMessenger.domain.broker.listener;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaGroup;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.domain.client.LogClient;
import com.nagornov.CorporateMessenger.domain.model.log.Log;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogListener {

    private final LogClient logClient;

    @KafkaListener(
            topics = KafkaTopic.LOG_TOPIC_NAME,
            groupId = KafkaGroup.LOG_GROUP_NAME,
            containerFactory = "kafkaLogContainerFactory"
    )
    public void distributor(ConsumerRecord<String, Log> record, Acknowledgment ack) {
        Log log = record.value();
        logClient.sendLog(log);
        ack.acknowledge();
    }

}