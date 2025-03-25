package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageFileByMessageIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraMessageFileMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraMessageFileByMessageIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraMessageFileByMessageIdRepository {

    private final SpringDataCassandraMessageFileByMessageIdRepository springDataCassandraMessageFileByMessageIdRepository;
    private final CassandraMessageFileMapper cassandraMessageFileMapper;

    public MessageFile save(MessageFile messageFile) {
        CassandraMessageFileByMessageIdEntity entity =
                springDataCassandraMessageFileByMessageIdRepository.save(
                        cassandraMessageFileMapper.toMessageFileByMessageIdEntity(messageFile)
                );
        return cassandraMessageFileMapper.toDomain(entity);
    }

    public void delete(MessageFile messageFile) {
        springDataCassandraMessageFileByMessageIdRepository.delete(
                cassandraMessageFileMapper.toMessageFileByMessageIdEntity(messageFile)
        );
    }

    public List<MessageFile> getAllByMessageId(UUID messageId) {
        return springDataCassandraMessageFileByMessageIdRepository
                .getAllByMessageId(messageId)
                .stream()
                .map(cassandraMessageFileMapper::toDomain)
                .toList();
    }

    public Optional<MessageFile> findByIdAndMessageId(UUID id, UUID messageId) {
        return springDataCassandraMessageFileByMessageIdRepository
                .findByIdAndMessageId(id, messageId)
                .map(cassandraMessageFileMapper::toDomain);
    }

}
