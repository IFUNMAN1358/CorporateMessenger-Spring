package com.nagornov.CorporateMessenger.infrastructure.configuration.db.postgres;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.nagornov.CorporateMessenger.infrastructure.persistence.jpa")
public class JpaConfiguration {
}
