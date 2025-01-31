package com.nagornov.CorporateMessenger.infrastructure.configuration.db.kafka;

import com.nagornov.CorporateMessenger.domain.enums.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic unreadMessageTopic() {
        return new NewTopic(KafkaTopic.UNREAD_MESSAGE_TOPIC.getName(), 1, (short) 1);
    }

    @Bean
    public NewTopic testTopic() {
        return new NewTopic(KafkaTopic.TEST_TOPIC.getName(), 1, (short) 1);
    }
}
