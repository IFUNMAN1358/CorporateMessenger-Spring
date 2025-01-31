package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.MessageFile;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageFileByMessageIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraMessageFileMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraMessageFileByMessageIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraMessageFileByMessageIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraMessageFileByMessageIdRepository springDataCassandraMessageFileByMessageIdRepository;
    private final CassandraMessageFileMapper cassandraMessageFileMapper;

    public void saveWithoutCheck(@NotNull MessageFile messageFile) {
        final CassandraMessageFileByMessageIdEntity entity =
                cassandraMessageFileMapper.toMessageFileByMessageIdEntity(messageFile);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull MessageFile messageFile) {
        final CassandraMessageFileByMessageIdEntity entity =
                cassandraMessageFileMapper.toMessageFileByMessageIdEntity(messageFile);
        cassandraTemplate.update(entity);
    }

    public void deleteWithoutCheck(@NotNull MessageFile messageFile) {
        final CassandraMessageFileByMessageIdEntity entity =
                cassandraMessageFileMapper.toMessageFileByMessageIdEntity(messageFile);
        cassandraTemplate.delete(entity);
    }

    public List<MessageFile> getAllByMessageId(@NotNull UUID messageId) {
        return springDataCassandraMessageFileByMessageIdRepository
                .getAllByMessageId(messageId)
                .stream()
                .map(cassandraMessageFileMapper::toDomain)
                .toList();
    }

    public Optional<MessageFile> findByIdAndMessageId(@NotNull UUID id, @NotNull UUID messageId) {
        return springDataCassandraMessageFileByMessageIdRepository
                .findByIdAndMessageId(id, messageId)
                .map(cassandraMessageFileMapper::toDomain);
    }

}
