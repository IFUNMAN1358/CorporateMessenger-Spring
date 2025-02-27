package com.nagornov.CorporateMessenger.sharedKernel.LogService.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "log-service")
@Getter
@Setter
public class LogProperties {

    private String url;

    private String apiKey;

    private List<String> level = List.of();

}
