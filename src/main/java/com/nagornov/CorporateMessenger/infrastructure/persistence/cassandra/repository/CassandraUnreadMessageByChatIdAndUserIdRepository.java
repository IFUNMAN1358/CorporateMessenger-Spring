package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraUnreadMessageByChatIdAndUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraUnreadMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraUnreadMessageByChatIdAndUserIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraUnreadMessageByChatIdAndUserIdRepository {

    private final SpringDataCassandraUnreadMessageByChatIdAndUserIdRepository springDataCassandraUnreadMessageByChatIdAndUserIdRepository;
    private final CassandraUnreadMessageMapper cassandraUnreadMessageMapper;


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

    public Optional<UnreadMessage> findByChatIdAndUserId(UUID chatId, UUID userId) {
        return springDataCassandraUnreadMessageByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .map(cassandraUnreadMessageMapper::toDomain);
    }

}
