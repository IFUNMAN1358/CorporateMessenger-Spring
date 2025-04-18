package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatByIdEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCassandraChatByIdRepository
        extends CassandraRepository<CassandraChatByIdEntity, Long> {
}
