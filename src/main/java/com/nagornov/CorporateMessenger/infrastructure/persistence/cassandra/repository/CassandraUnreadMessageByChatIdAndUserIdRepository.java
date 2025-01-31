package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.UnreadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraUnreadMessageByChatIdAndUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraUnreadMessageMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraUnreadMessageByChatIdAndUserIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraUnreadMessageByChatIdAndUserIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraUnreadMessageByChatIdAndUserIdRepository
            springDataCassandraUnreadMessageByChatIdAndUserIdRepository;
    private final CassandraUnreadMessageMapper cassandraUnreadMessageMapper;


    public void saveWithoutCheck(@NotNull UnreadMessage unreadMessage) {
        final CassandraUnreadMessageByChatIdAndUserIdEntity entity =
                cassandraUnreadMessageMapper.toUnreadMessageByChatIdAndUserIdEntity(unreadMessage);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull UnreadMessage unreadMessage) {
        final CassandraUnreadMessageByChatIdAndUserIdEntity entity =
                cassandraUnreadMessageMapper.toUnreadMessageByChatIdAndUserIdEntity(unreadMessage);
        cassandraTemplate.update(entity);
    }

    public void deleteWithoutCheck(@NotNull UnreadMessage unreadMessage) {
        final CassandraUnreadMessageByChatIdAndUserIdEntity entity =
                cassandraUnreadMessageMapper.toUnreadMessageByChatIdAndUserIdEntity(unreadMessage);
        cassandraTemplate.delete(entity);
    }

    public Optional<UnreadMessage> findByChatIdAndUserId(@NotNull UUID chatId, @NotNull UUID userId) {
        return springDataCassandraUnreadMessageByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .map(cassandraUnreadMessageMapper::toDomain);
    }

}
