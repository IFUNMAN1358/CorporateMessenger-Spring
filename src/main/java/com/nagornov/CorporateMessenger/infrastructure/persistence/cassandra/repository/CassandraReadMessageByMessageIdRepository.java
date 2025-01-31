package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.ReadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraReadMessageByMessageIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraReadMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraReadMessageByMessageIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraReadMessageByMessageIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraReadMessageByMessageIdRepository springDataCassandraReadMessageByMessageIdRepository;
    private final CassandraReadMessageMapper cassandraReadMessageMapper;

    public void saveWithoutCheck(@NotNull ReadMessage readMessage) {
        final CassandraReadMessageByMessageIdEntity entity =
                cassandraReadMessageMapper.toReadMessageByMessageIdEntity(readMessage);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull ReadMessage readMessage) {
        final CassandraReadMessageByMessageIdEntity entity =
                cassandraReadMessageMapper.toReadMessageByMessageIdEntity(readMessage);
        cassandraTemplate.update(entity);
    }

    public void deleteWithoutCheck(@NotNull ReadMessage readMessage) {
        final CassandraReadMessageByMessageIdEntity entity =
                cassandraReadMessageMapper.toReadMessageByMessageIdEntity(readMessage);
        cassandraTemplate.delete(entity);
    }

    public List<ReadMessage> getAllByMessageId(@NotNull UUID messageId) {
        return springDataCassandraReadMessageByMessageIdRepository
                .getAllByMessageId(messageId)
                .stream()
                .map(cassandraReadMessageMapper::toDomain)
                .toList();
    }

}
