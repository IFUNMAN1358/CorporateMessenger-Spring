package com.nagornov.CorporateMessenger.auth.infrastructure.configuration;

import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData.AuthSpringDataJpaRegistrationKeyRepository;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData.AuthSpringDataJpaRoleRepository;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData.AuthSpringDataJpaSessionRepository;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.springData.AuthSpringDataJpaUserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackageClasses = {
                AuthSpringDataJpaUserRepository.class,
                AuthSpringDataJpaRoleRepository.class,
                AuthSpringDataJpaRegistrationKeyRepository.class,
                AuthSpringDataJpaSessionRepository.class
        }
)
public class AuthJpaConfiguration {
}
