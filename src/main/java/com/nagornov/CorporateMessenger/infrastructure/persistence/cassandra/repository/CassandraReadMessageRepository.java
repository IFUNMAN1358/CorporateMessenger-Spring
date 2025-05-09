package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.message.ReadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraReadMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraReadMessageByMessageIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraReadMessageRepository {

    private final SpringDataCassandraReadMessageByMessageIdRepository springDataCassandraReadMessageByMessageIdRepository;
    private final CassandraReadMessageMapper cassandraReadMessageMapper;

    public ReadMessage save(ReadMessage readMessage) {
        return cassandraReadMessageMapper.toDomain(
                springDataCassandraReadMessageByMessageIdRepository.save(
                        cassandraReadMessageMapper.toReadMessageByMessageIdEntity(readMessage)
                )
        );
    }

    public void delete(ReadMessage readMessage) {
        springDataCassandraReadMessageByMessageIdRepository.delete(
                cassandraReadMessageMapper.toReadMessageByMessageIdEntity(readMessage)
        );
    }

    public void deleteAllByMessageId(UUID messageId) {
        springDataCassandraReadMessageByMessageIdRepository.deleteAllByMessageId(messageId);
    }

    public List<ReadMessage> findAllByMessageId(UUID messageId) {
        return springDataCassandraReadMessageByMessageIdRepository
                .findAllByMessageId(messageId)
                .stream()
                .map(cassandraReadMessageMapper::toDomain)
                .toList();
    }
}
