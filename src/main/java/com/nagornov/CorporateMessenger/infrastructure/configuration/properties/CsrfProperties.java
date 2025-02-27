package com.nagornov.CorporateMessenger.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "csrf")
@Getter
@Setter
public class CsrfProperties {

    private String headerName;
    private Cookie cookie;

    @Setter
    @Getter
    public static class Cookie {

        private String name;
        private String domain;
        private String path;
        private Boolean httpOnly;
        private Boolean secure;
        private String sameSite;

    }

}
