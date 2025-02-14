package com.nagornov.CorporateMessenger.infrastructure.configuration.db.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.props.KafkaProperties;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.serializer.KafkaUnreadMessageDTOSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaUnreadMessageProducer {

    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public KafkaTemplate<String, KafkaUnreadMessageDTO> kafkaUnreadMessageTemplate() {
        return new KafkaTemplate<>(kafkaUnreadMessageProducerFactory());
    }

    @Bean
    public ProducerFactory<String, KafkaUnreadMessageDTO> kafkaUnreadMessageProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000);
        configProps.put(ProducerConfig.RETRIES_CONFIG, 0);

        return new DefaultKafkaProducerFactory<>(
                configProps,
                new StringSerializer(),
                new KafkaUnreadMessageDTOSerializer(objectMapper)
        );
    }
}
