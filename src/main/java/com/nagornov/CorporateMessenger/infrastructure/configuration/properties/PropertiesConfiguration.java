package com.nagornov.CorporateMessenger.infrastructure.configuration.properties;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.props.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ServiceProperties.class,
        JwtProperties.class,
        CsrfProperties.class,
        CassandraProperties.class,
        RedisProperties.class,
        MinioProperties.class,
        KafkaProperties.class
})
public class PropertiesConfiguration {
}
