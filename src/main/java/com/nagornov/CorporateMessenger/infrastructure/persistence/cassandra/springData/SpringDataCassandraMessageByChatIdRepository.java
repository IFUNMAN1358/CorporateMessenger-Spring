package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraMessageByChatIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataCassandraMessageByChatIdRepository
        extends CassandraRepository<CassandraMessageByChatIdEntity, CassandraMessageByChatIdKey> {

    @Query("select * from messages_by_chat_id where chat_id = :chatId")
    Slice<CassandraMessageByChatIdEntity> getAllMessagesByChatId(@Param("chatId") Long chatId, Pageable pageable);

    @Query("select * from messages_by_chat_id where chat_id = :chatId limit 1")
    Optional<CassandraMessageByChatIdEntity> findLastMessageByChatId(@Param("chatId") Long chatId);

}
