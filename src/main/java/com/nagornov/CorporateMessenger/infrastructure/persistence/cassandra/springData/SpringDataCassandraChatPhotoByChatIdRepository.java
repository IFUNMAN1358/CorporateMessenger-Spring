package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatPhotoByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraChatPhotoByChatIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCassandraChatPhotoByChatIdRepository
        extends CassandraRepository<CassandraChatPhotoByChatIdEntity, CassandraChatPhotoByChatIdKey> {

    @Query("SELECT * FROM chat_photos_by_chat_id WHERE chat_id = :chatId")
    Optional<CassandraChatPhotoByChatIdEntity> findByChatId(@Param("chatId") Long chatId);

    @Query("SELECT * FROM chat_photos_by_chat_id WHERE id = :id and chat_id = :chatId")
    Optional<CassandraChatPhotoByChatIdEntity> findByIdAndChatId(
            @Param("id") UUID id,
            @Param("chatId") Long chatId
    );

}
