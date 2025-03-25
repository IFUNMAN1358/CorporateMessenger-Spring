package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraMessageByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraMessageByIdRepository {

    private final SpringDataCassandraMessageByIdRepository springDataCassandraMessageByIdRepository;
    private final CassandraMessageMapper cassandraMessageMapper;


    public Message save(Message message) {
        CassandraMessageByIdEntity entity =
                springDataCassandraMessageByIdRepository.save(
                        cassandraMessageMapper.toMessageByIdEntity(message)
                );
        return cassandraMessageMapper.toDomain(entity);
    }

    public void delete(Message message) {
        springDataCassandraMessageByIdRepository.delete(
                cassandraMessageMapper.toMessageByIdEntity(message)
        );
    }

    public Optional<Message> findById(UUID id) {
        return springDataCassandraMessageByIdRepository
                .findCassandraMessageEntityById(id)
                .map(cassandraMessageMapper::toDomain);
    }

}
