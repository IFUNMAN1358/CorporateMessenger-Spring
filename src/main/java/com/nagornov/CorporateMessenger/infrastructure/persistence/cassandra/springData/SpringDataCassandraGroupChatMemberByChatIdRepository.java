package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraGroupChatMemberByChatIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCassandraGroupChatMemberByChatIdRepository
        extends CassandraRepository<CassandraGroupChatMemberByChatIdEntity, CassandraGroupChatMemberByChatIdKey> {

    @Query("select * from group_chat_members_by_chat_id where chat_id = :chatId")
    List<CassandraGroupChatMemberByChatIdEntity> getAllByChatId(@Param("chatId") UUID chatId);

}
