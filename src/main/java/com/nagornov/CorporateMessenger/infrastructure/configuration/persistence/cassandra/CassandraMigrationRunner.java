package com.nagornov.CorporateMessenger.infrastructure.configuration.persistence.cassandra;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.cognitor.cassandra.migration.MigrationTask;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CassandraMigrationRunner {

    private final MigrationTask migrationTask;

    @PostConstruct
    public void migrate() {
        migrationTask.migrate();
    }
}