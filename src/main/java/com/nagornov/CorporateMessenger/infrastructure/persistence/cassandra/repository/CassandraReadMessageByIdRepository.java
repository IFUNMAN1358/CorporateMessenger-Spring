package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.ReadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraReadMessageByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraReadMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraReadMessageByIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraReadMessageByIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraReadMessageByIdRepository springDataCassandraReadMessageByIdRepository;
    private final CassandraReadMessageMapper cassandraReadMessageMapper;

    public Optional<ReadMessage> findById(@NotNull UUID id) {
        return springDataCassandraReadMessageByIdRepository.findById(id)
                .map(cassandraReadMessageMapper::toDomain);
    }

    public void saveWithoutCheck(@NotNull ReadMessage readMessage) {
        final CassandraReadMessageByIdEntity entity =
                cassandraReadMessageMapper.toReadMessageByIdEntity(readMessage);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull ReadMessage readMessage) {
        final CassandraReadMessageByIdEntity entity =
                cassandraReadMessageMapper.toReadMessageByIdEntity(readMessage);
        cassandraTemplate.update(entity);
    }

    public void deleteWithoutCheck(@NotNull ReadMessage readMessage) {
        final CassandraReadMessageByIdEntity entity =
                cassandraReadMessageMapper.toReadMessageByIdEntity(readMessage);
        cassandraTemplate.delete(entity);
    }
}
