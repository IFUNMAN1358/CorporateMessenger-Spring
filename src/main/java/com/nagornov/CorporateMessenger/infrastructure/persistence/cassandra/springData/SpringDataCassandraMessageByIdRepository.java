package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageByIdEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCassandraMessageByIdRepository
        extends CassandraRepository<CassandraMessageByIdEntity, UUID> {

    @Query("select * from messages_by_id where id = :id")
    Optional<CassandraMessageByIdEntity> findCassandraMessageEntityById(@Param("id") UUID id);
}
