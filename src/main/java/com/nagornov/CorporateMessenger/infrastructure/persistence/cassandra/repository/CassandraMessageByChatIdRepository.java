package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraMessageByChatIdRepository;
import lombok.RequiredArgsConstructor;
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

    private final SpringDataCassandraMessageByChatIdRepository springDataCassandraMessageByChatIdRepository;
    private final CassandraMessageMapper cassandraMessageMapper;

    public Message save(Message message) {
        CassandraMessageByChatIdEntity entity =
                springDataCassandraMessageByChatIdRepository.save(
                        cassandraMessageMapper.toMessageByChatIdEntity(message)
                );
        return cassandraMessageMapper.toDomain(entity);
    }

    public void delete(Message message) {
        springDataCassandraMessageByChatIdRepository.delete(
                cassandraMessageMapper.toMessageByChatIdEntity(message)
        );
    }

    public Optional<Message> findLastByChatId(UUID chatId) {
        return springDataCassandraMessageByChatIdRepository
                .findLastMessageByChatId(chatId)
                .map(cassandraMessageMapper::toDomain);
    }

    public List<Message> getAllByChatId(UUID chatId,  int page,  int size) {
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
