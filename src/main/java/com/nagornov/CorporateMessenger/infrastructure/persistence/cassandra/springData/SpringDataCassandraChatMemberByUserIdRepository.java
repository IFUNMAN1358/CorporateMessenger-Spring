package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatMemberByUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraChatMemberByUserIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCassandraChatMemberByUserIdRepository
        extends CassandraRepository<CassandraChatMemberByUserIdEntity, CassandraChatMemberByUserIdKey> {

    @Query("SELECT * FROM chat_members_by_user_id WHERE user_id = :userId")
    List<CassandraChatMemberByUserIdEntity> findAllByUserId(@Param("userId") UUID userId);

    @Query("DELETE FROM chat_members_by_user_id WHERE chat_id = :chatId AND user_id IN :userIds")
    void deleteAllByChatIdAndUserIds(@Param("chatId") Long chatId, @Param("userIds") List<UUID> userIds);

}
