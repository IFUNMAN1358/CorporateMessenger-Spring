package com.nagornov.CorporateMessenger.infrastructure.configuration.persistence.kafka;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic unreadMessageTopic() {
        return new NewTopic(KafkaTopic.UNREAD_MESSAGE_TOPIC_NAME, 1, (short) 3);
    }

    @Bean
    public NewTopic logTopic() {
        return new NewTopic(KafkaTopic.LOG_TOPIC_NAME, 1, (short) 3);
    }

    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic(KafkaTopic.NOTIFICATION_TOPIC_NAME, 4, (short) 3);
    }
}
