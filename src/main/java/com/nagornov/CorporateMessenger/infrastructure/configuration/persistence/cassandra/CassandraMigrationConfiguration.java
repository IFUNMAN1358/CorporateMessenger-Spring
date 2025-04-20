package com.nagornov.CorporateMessenger.infrastructure.configuration.persistence.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.CassandraProperties;
import lombok.RequiredArgsConstructor;
import org.cognitor.cassandra.migration.Database;
import org.cognitor.cassandra.migration.MigrationConfiguration;
import org.cognitor.cassandra.migration.MigrationRepository;
import org.cognitor.cassandra.migration.MigrationTask;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@RequiredArgsConstructor
public class CassandraMigrationConfiguration {

    private final CassandraProperties cassandraProperties;

    @Bean(name = "migrationCqlSession")
    public CqlSession migrationCqlSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(
                        cassandraProperties.getContactPoints(),
                        cassandraProperties.getPort())
                )
                .withAuthCredentials(
                        cassandraProperties.getUsername(),
                        cassandraProperties.getPassword()
                )
                .withLocalDatacenter(cassandraProperties.getLocalDatacenter())
                .build();
    }

    @Bean
    public MigrationTask migrationTask(
            MigrationConfiguration migrationConfiguration,
            @Qualifier("migrationCqlSession") CqlSession migrationCqlSession
    ) {
        Database database = new Database(migrationCqlSession, migrationConfiguration);
        MigrationRepository migrationRepository = new MigrationRepository("/db/cassandra");
        return new MigrationTask(database, migrationRepository);
    }

    @Bean
    public MigrationConfiguration migrationConfiguration() {
        MigrationConfiguration config = new MigrationConfiguration();
        return config.withKeyspaceName(cassandraProperties.getKeyspaceName());
    }
}