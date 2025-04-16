package com.nagornov.CorporateMessenger.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    private String accessSecret;
    private String refreshSecret;
    private Integer accessExpire;
    private Integer refreshExpire;

}
