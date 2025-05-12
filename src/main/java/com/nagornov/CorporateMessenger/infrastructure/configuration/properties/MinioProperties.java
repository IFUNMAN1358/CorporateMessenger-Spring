package com.nagornov.CorporateMessenger.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinioProperties {

    private String uri;
    private String host;
    private int port;
    private Boolean secure;
    private String accessKey;
    private String secretKey;
    private String user;
    private String password;

}
