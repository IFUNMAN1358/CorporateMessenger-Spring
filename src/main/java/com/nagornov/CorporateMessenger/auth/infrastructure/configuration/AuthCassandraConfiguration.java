package com.nagornov.CorporateMessenger.auth.infrastructure.configuration;

import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.springData.AuthSpringDataCassandraUserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(
        basePackageClasses = AuthSpringDataCassandraUserRepository.class
)
public class AuthCassandraConfiguration {
}
