package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraMessageByChatIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraMessageByChatIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraMessageByChatIdRepository springDataCassandraMessageByChatIdRepository;
    private final CassandraMessageMapper cassandraMessageMapper;

    public void saveWithoutCheck(@NotNull Message message) {
        final CassandraMessageByChatIdEntity entity =
                cassandraMessageMapper.toMessageByChatIdEntity(message);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull Message message) {
        final CassandraMessageByChatIdEntity entity =
                cassandraMessageMapper.toMessageByChatIdEntity(message);
        cassandraTemplate.update(entity);
    }

    public void deleteWithoutCheck(@NotNull Message message) {
        final CassandraMessageByChatIdEntity entity =
                cassandraMessageMapper.toMessageByChatIdEntity(message);
        cassandraTemplate.delete(entity);
    }

    public Optional<Message> findLastByChatId(@NotNull UUID chatId) {
        return springDataCassandraMessageByChatIdRepository
                .findLastMessageByChatId(chatId)
                .map(cassandraMessageMapper::toDomain);
    }

    public List<Message> getAllByChatId(@NotNull UUID chatId, @NotNull int page, @NotNull int size) {
        Pageable pageable = CassandraPageRequest.of(0, size);

        for (int i = 0; i < page; i++) {
            Slice<CassandraMessageByChatIdEntity> slice = springDataCassandraMessageByChatIdRepository
                    .getAllMessagesByChatId(chatId, pageable);
            if (!slice.hasNext()) {
                return Collections.emptyList();
            }
            pageable = slice.nextPageable();
        }

        return springDataCassandraMessageByChatIdRepository
                .getAllMessagesByChatId(chatId, pageable)
                .stream()
                .map(cassandraMessageMapper::toDomain)
                .toList();
    }

}
