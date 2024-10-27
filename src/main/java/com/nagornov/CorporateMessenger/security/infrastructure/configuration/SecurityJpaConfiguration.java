package com.nagornov.CorporateMessenger.security.infrastructure.configuration;

import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.springData.SecuritySpringDataJpaSessionRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackageClasses = {
                SecuritySpringDataJpaSessionRepository.class
        }
)
public class SecurityJpaConfiguration {
}
