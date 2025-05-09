package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatMemberByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraChatMemberByChatIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCassandraChatMemberByChatIdRepository
        extends CassandraRepository<CassandraChatMemberByChatIdEntity, CassandraChatMemberByChatIdKey> {

    @Query("SELECT * FROM chat_members_by_chat_id WHERE chat_id = :chatId")
    List<CassandraChatMemberByChatIdEntity> findAllByChatId(@Param("chatId") Long chatId);

    @Query("SELECT * FROM chat_members_by_chat_id WHERE chat_id = :chatId")
    Slice<CassandraChatMemberByChatIdEntity> findAllByChatId(@Param("chatId") Long chatId, Pageable pageable);

    @Query("SELECT * FROM chat_members_by_chat_id WHERE chat_id = :chatId AND user_id = :userId")
    Optional<CassandraChatMemberByChatIdEntity> findByChatIdAndUserId(
            @Param("chatId") Long chatId,
            @Param("userId") UUID userId
    );

    @Query("DELETE FROM chat_members_by_chat_id WHERE chat_id = :chatId AND user_id IN :userIds")
    void deleteAllByChatIdAndUserIds(@Param("chatId") Long chatId, @Param("userIds") List<UUID> userIds);

    @Query("SELECT COUNT(*) > 0 FROM chat_members_by_chat_id WHERE chat_id = :chatId AND user_id = :userId")
    boolean existsByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") UUID userId);
}
