package com.nagornov.CorporateMessenger.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "external-service")
@Getter
@Setter
public class ExternalServiceProperties {

    private HeaderName headerName;
    private Redis redis;

    @Getter
    @Setter
    public static class HeaderName {

        private String traceId;
        private String serviceName;
        private String apiKey;

    }

    @Getter
    @Setter
    public static class Redis {

        private Long timeout;
        private TimeUnit timeUnitSeconds = TimeUnit.SECONDS;

    }

}
