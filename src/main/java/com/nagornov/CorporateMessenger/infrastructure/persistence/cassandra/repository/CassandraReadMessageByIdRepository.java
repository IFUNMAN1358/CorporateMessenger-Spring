package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.message.ReadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraReadMessageByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraReadMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraReadMessageByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraReadMessageByIdRepository {

    private final SpringDataCassandraReadMessageByIdRepository springDataCassandraReadMessageByIdRepository;
    private final CassandraReadMessageMapper cassandraReadMessageMapper;

    public ReadMessage save(ReadMessage readMessage) {
        CassandraReadMessageByIdEntity entity =
                springDataCassandraReadMessageByIdRepository.save(
                        cassandraReadMessageMapper.toReadMessageByIdEntity(readMessage)
                );
        return cassandraReadMessageMapper.toDomain(entity);
    }

    public void delete(ReadMessage readMessage) {
        springDataCassandraReadMessageByIdRepository.delete(
                cassandraReadMessageMapper.toReadMessageByIdEntity(readMessage)
        );
    }

    public Optional<ReadMessage> findById(UUID id) {
        return springDataCassandraReadMessageByIdRepository.findById(id)
                .map(cassandraReadMessageMapper::toDomain);
    }
}
