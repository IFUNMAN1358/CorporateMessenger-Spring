package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.message.ReadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraReadMessageByMessageIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraReadMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraReadMessageByIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraReadMessageByMessageIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraReadMessageRepository {

    private final SpringDataCassandraReadMessageByIdRepository springDataCassandraReadMessageByIdRepository;
    private final SpringDataCassandraReadMessageByMessageIdRepository springDataCassandraReadMessageByMessageIdRepository;
    private final CassandraReadMessageMapper cassandraReadMessageMapper;

    public ReadMessage save(ReadMessage readMessage) {
        springDataCassandraReadMessageByIdRepository.save(
                cassandraReadMessageMapper.toReadMessageByIdEntity(readMessage)
        );
        CassandraReadMessageByMessageIdEntity entity =
                springDataCassandraReadMessageByMessageIdRepository.save(
                        cassandraReadMessageMapper.toReadMessageByMessageIdEntity(readMessage)
                );
        return cassandraReadMessageMapper.toDomain(entity);
    }

    public void delete(ReadMessage readMessage) {
        springDataCassandraReadMessageByIdRepository.delete(
                cassandraReadMessageMapper.toReadMessageByIdEntity(readMessage)
        );
        springDataCassandraReadMessageByMessageIdRepository.delete(
                cassandraReadMessageMapper.toReadMessageByMessageIdEntity(readMessage)
        );
    }

    public Optional<ReadMessage> findById(UUID id) {
        return springDataCassandraReadMessageByIdRepository.findById(id)
                .map(cassandraReadMessageMapper::toDomain);
    }

    public List<ReadMessage> getAllByMessageId(UUID messageId) {
        return springDataCassandraReadMessageByMessageIdRepository
                .getAllByMessageId(messageId)
                .stream()
                .map(cassandraReadMessageMapper::toDomain)
                .toList();
    }
}
