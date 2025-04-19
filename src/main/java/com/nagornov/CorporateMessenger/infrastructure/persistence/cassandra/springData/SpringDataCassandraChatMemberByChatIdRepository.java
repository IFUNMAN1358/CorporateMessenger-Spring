package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatMemberByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraChatMemberByChatIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
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

    @Query("SELECT * FROM chat_members_by_chat_id WHERE chat_id = :chatId AND user_id = :userId")
    Optional<CassandraChatMemberByChatIdEntity> findByChatIdAndUserId(
            @Param("chatId") Long chatId,
            @Param("userId") UUID userId
    );
}
