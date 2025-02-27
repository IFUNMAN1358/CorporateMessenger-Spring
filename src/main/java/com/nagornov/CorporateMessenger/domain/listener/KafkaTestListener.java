package com.nagornov.CorporateMessenger.domain.listener;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaGroup;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTestListener {

    @KafkaListener(
            topics = KafkaTopic.TEST_TOPIC_NAME,
            groupId = KafkaGroup.TEST_GROUP_NAME,
            containerFactory = "kafkaTestContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            System.out.println("Processing message: " + record.value());
            ack.acknowledge();
        } catch (Exception e) {
            System.out.println("Error processing message: " + e.getMessage());
        }
    }

}
