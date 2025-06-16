package com.nagornov.CorporateMessenger.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "session")
@Getter
@Setter
public class SessionProperties {

    private String headerName;
    private Integer expire;

}
