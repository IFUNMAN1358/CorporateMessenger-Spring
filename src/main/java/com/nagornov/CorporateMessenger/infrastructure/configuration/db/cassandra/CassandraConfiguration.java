package com.nagornov.CorporateMessenger.infrastructure.configuration.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.CassandraProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.DefaultCqlBeanNames;

import java.net.InetSocketAddress;

@Configuration
@RequiredArgsConstructor
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    private final CassandraProperties cassandraProperties;

    @Bean
    @Primary
    public CqlSession mainCqlSession() {
        return CqlSession.builder()
                .withKeyspace(cassandraProperties.getKeyspaceName())
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

    @Bean(DefaultCqlBeanNames.SESSION)
    @Primary
    public CqlSession cassandraSession(CqlSessionBuilder cqlSessionBuilder) {
        return cqlSessionBuilder.build();
    }

    @Override
    protected String getKeyspaceName() {
        return cassandraProperties.getKeyspaceName();
    }

    @Override
    protected String getContactPoints() {
        return cassandraProperties.getContactPoints();
    }

    @Override
    protected int getPort() {
        return cassandraProperties.getPort();
    }

    @Override
    protected String getLocalDataCenter() {
        return cassandraProperties.getLocalDatacenter();
    }

}
