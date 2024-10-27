package com.nagornov.CorporateMessenger.sharedKernel.properties.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cassandra")
@Getter
@Setter
public class CassandraProperties {

    private String contactPoints;
    private Integer port;
    private String keyspaceName;
    private String username;
    private String password;
    private String localDatacenter;
    private String schemaAction;
    private ssl ssl;

    @Getter
    @Setter
    static class ssl {
        private Boolean enabled;
    }

}
