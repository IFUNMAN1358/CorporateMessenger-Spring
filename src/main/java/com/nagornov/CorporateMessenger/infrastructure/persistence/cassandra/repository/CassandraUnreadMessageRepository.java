package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraUnreadMessageByChatIdAndUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraUnreadMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraUnreadMessageByChatIdAndUserIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraUnreadMessageRepository {

    private final SpringDataCassandraUnreadMessageByChatIdAndUserIdRepository springDataCassandraUnreadMessageByChatIdAndUserIdRepository;
    private final CassandraUnreadMessageMapper cassandraUnreadMessageMapper;

    public List<UnreadMessage> saveAll(List<UnreadMessage> unreadMessages) {
        return springDataCassandraUnreadMessageByChatIdAndUserIdRepository.saveAll(
                unreadMessages.stream().map(cassandraUnreadMessageMapper::toUnreadMessageByChatIdAndUserIdEntity).toList()
        )
        .stream().map(cassandraUnreadMessageMapper::toDomain).toList();
    }

    public UnreadMessage save(UnreadMessage unreadMessage) {
        CassandraUnreadMessageByChatIdAndUserIdEntity entity =
                springDataCassandraUnreadMessageByChatIdAndUserIdRepository.save(
                        cassandraUnreadMessageMapper.toUnreadMessageByChatIdAndUserIdEntity(unreadMessage)
                );
        return cassandraUnreadMessageMapper.toDomain(entity);
    }

    public void delete(UnreadMessage unreadMessage) {
        springDataCassandraUnreadMessageByChatIdAndUserIdRepository.delete(
                cassandraUnreadMessageMapper.toUnreadMessageByChatIdAndUserIdEntity(unreadMessage)
        );
    }

    public void deleteByChatIdAndUserId(Long chatId, UUID userId) {
        springDataCassandraUnreadMessageByChatIdAndUserIdRepository.deleteByChatIdAndUserId(
                chatId, userId
        );
    }

    public void deleteAllByChatIdAndUserIds(Long chatId, List<UUID> userIds) {
        springDataCassandraUnreadMessageByChatIdAndUserIdRepository.deleteAllByChatIdAndUserIds(chatId, userIds);
    }

    public Optional<UnreadMessage> findByChatIdAndUserId(Long chatId, UUID userId) {
        return springDataCassandraUnreadMessageByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .map(cassandraUnreadMessageMapper::toDomain);
    }
}
