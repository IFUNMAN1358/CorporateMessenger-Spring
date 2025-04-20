package com.nagornov.CorporateMessenger.infrastructure.configuration.persistence.kafka;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin(ProducerFactory<String, String> producerFactory) {

        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 60000);

        return new KafkaAdmin(configs);
    }

}
