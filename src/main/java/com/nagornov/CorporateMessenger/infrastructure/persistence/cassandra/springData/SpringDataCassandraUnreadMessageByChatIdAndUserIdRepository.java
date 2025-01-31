package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraUnreadMessageByChatIdAndUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraUnreadMessageByChatIdAndUserIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCassandraUnreadMessageByChatIdAndUserIdRepository
        extends CassandraRepository<CassandraUnreadMessageByChatIdAndUserIdEntity, CassandraUnreadMessageByChatIdAndUserIdKey> {

    @Query("select * from unread_messages_by_chat_id_and_user_id where chat_id = :chatId and user_id = :userId")
    Optional<CassandraUnreadMessageByChatIdAndUserIdEntity> findByChatIdAndUserId(
            @Param("chatId") UUID chatId, @Param("userId") UUID userId
    );

}
