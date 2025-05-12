package com.nagornov.CorporateMessenger.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "external-service")
@Getter
@Setter
public class ExternalServiceProperties {

    private HeaderName headerName;

    @Getter
    @Setter
    public static class HeaderName {

        private String traceId;
        private String serviceName;
        private String apiKey;

    }

}
