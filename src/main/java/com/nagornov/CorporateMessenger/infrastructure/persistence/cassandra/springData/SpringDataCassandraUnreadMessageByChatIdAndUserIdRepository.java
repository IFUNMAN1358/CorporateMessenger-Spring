package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraUnreadMessageByChatIdAndUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraUnreadMessageByChatIdAndUserIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCassandraUnreadMessageByChatIdAndUserIdRepository
        extends CassandraRepository<CassandraUnreadMessageByChatIdAndUserIdEntity, CassandraUnreadMessageByChatIdAndUserIdKey> {

    @Query("SELECT * FROM unread_messages_by_chat_id_and_user_id WHERE chat_id = :chatId AND user_id = :userId")
    Optional<CassandraUnreadMessageByChatIdAndUserIdEntity> findByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") UUID userId);

    @Query("DELETE FROM unread_messages_by_chat_id_and_user_id WHERE chat_id = :chatId AND user_id = :userId")
    void deleteByChatIdAndUserId(@Param("chatId") Long chatId, @Param("") UUID userId);

    @Query("DELETE FROM unread_messages_by_chat_id_and_user_id WHERE chat_id = :chatId AND user_id IN :userIds")
    void deleteAllByChatIdAndUserIds(@Param("chatId") Long chatId, @Param("userIds") List<UUID> userIds);

}
