package com.nagornov.CorporateMessenger.sharedKernel.properties.configuration;

import com.nagornov.CorporateMessenger.sharedKernel.properties.props.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ServiceProperties.class,
        JwtProperties.class,
        CsrfProperties.class,
        CassandraProperties.class,
        RedisProperties.class
})
public class PropertiesConfiguration {
}
