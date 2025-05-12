package com.nagornov.CorporateMessenger.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "log-service")
@Getter
@Setter
public class LogServiceProperties {

    private String url;
    private String serviceName;
    private String apiKey;

}
