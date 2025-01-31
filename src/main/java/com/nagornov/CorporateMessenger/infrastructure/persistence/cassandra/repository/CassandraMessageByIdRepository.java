package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraMessageByIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraMessageByIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraMessageByIdRepository springDataCassandraMessageByIdRepository;
    private final CassandraMessageMapper cassandraMessageMapper;


    public void saveWithoutCheck(@NotNull Message message) {
        final CassandraMessageByIdEntity entity =
                cassandraMessageMapper.toMessageByIdEntity(message);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull Message message) {
        final CassandraMessageByIdEntity entity =
                cassandraMessageMapper.toMessageByIdEntity(message);
        cassandraTemplate.update(entity);
    }

    public void deleteWithoutCheck(@NotNull Message message) {
        final CassandraMessageByIdEntity entity =
                cassandraMessageMapper.toMessageByIdEntity(message);
        cassandraTemplate.delete(entity);
    }

    public Optional<Message> findById(@NotNull UUID id) {
        return springDataCassandraMessageByIdRepository
                .findCassandraMessageEntityById(id)
                .map(cassandraMessageMapper::toDomain);
    }

}
