package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraMessageByChatIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraMessageByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraMessageRepository {

    private final SpringDataCassandraMessageByIdRepository springDataCassandraMessageByIdRepository;
    private final SpringDataCassandraMessageByChatIdRepository springDataCassandraMessageByChatIdRepository;
    private final CassandraMessageMapper cassandraMessageMapper;

    public Message save(Message message) {
        springDataCassandraMessageByIdRepository.save(
                cassandraMessageMapper.toMessageByIdEntity(message)
        );
        CassandraMessageByChatIdEntity entity = springDataCassandraMessageByChatIdRepository.save(
                cassandraMessageMapper.toMessageByChatIdEntity(message)
        );
        return cassandraMessageMapper.toDomain(entity);
    }

    public void delete(Message message) {
        springDataCassandraMessageByIdRepository.delete(
                cassandraMessageMapper.toMessageByIdEntity(message)
        );
        springDataCassandraMessageByChatIdRepository.delete(
                cassandraMessageMapper.toMessageByChatIdEntity(message)
        );
    }

    public Optional<Message> findLastByChatId(Long chatId) {
        return springDataCassandraMessageByChatIdRepository
                .findLastMessageByChatId(chatId)
                .map(cassandraMessageMapper::toDomain);
    }

    public List<Message> getAllByChatId(Long chatId, int page, int size) {
        Pageable pageable = CassandraPageRequest.of(page, size);
        return springDataCassandraMessageByChatIdRepository
                .getAllMessagesByChatId(chatId, pageable)
                .stream()
                .map(cassandraMessageMapper::toDomain)
                .toList();
    }

    public Optional<Message> findById(UUID id) {
        return springDataCassandraMessageByIdRepository
                .findCassandraMessageEntityById(id)
                .map(cassandraMessageMapper::toDomain);
    }

    public Optional<Message> findByChatIdAndId(Long chatId, UUID id) {
        return springDataCassandraMessageByChatIdRepository
                .findByChatIdAndId(chatId, id)
                .map(cassandraMessageMapper::toDomain);
    }
}
