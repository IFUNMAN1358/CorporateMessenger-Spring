package com.nagornov.CorporateMessenger.infrastructure.configuration.properties.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka")
@Getter
@Setter
public class KafkaProperties {

    private String bootstrapServers;

}
