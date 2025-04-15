package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatPhotoByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraGroupChatPhotoByChatIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCassandraGroupChatPhotoByChatIdRepository
        extends CassandraRepository<CassandraGroupChatPhotoByChatIdEntity, CassandraGroupChatPhotoByChatIdKey> {

    @Query("select * from group_chat_photos_by_chat_id where user_id = :userId")
    List<CassandraGroupChatPhotoByChatIdEntity> findAllByChatId(@Param("chatId") UUID chatId);
}
