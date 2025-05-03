package com.nagornov.CorporateMessenger.infrastructure.configuration.persistence.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaGroup;
import com.nagornov.CorporateMessenger.infrastructure.configuration.persistence.kafka.errorHandler.KafkaErrorHandler;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.KafkaProperties;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.deserializer.KafkaUnreadMessageDTODeserializer;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaUnreadMessageConsumer {

    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;
    private final KafkaErrorHandler kafkaErrorHandler;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaUnreadMessageDTO> kafkaUnreadMessageContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaUnreadMessageDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaUnreadMessageConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, KafkaUnreadMessageDTO> kafkaUnreadMessageConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaGroup.UNREAD_MESSAGE_GROUP_NAME);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                new KafkaUnreadMessageDTODeserializer(objectMapper)
        );
    }
}
