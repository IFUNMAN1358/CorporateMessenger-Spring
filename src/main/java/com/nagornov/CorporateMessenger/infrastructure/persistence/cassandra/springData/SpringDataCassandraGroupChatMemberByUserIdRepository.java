package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraGroupChatMemberByUserIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCassandraGroupChatMemberByUserIdRepository
        extends CassandraRepository<CassandraGroupChatMemberByUserIdEntity, CassandraGroupChatMemberByUserIdKey> {

    @Query("select * from group_chat_members_by_user_id where user_id = :userId")
    List<CassandraGroupChatMemberByUserIdEntity> getAllByUserId(@Param("userId") UUID userId);

}
