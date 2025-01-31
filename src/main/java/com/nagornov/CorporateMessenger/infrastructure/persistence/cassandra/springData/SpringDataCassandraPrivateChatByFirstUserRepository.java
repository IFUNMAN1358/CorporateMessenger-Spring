package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByFirstUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraPrivateChatByFirstUserKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCassandraPrivateChatByFirstUserRepository
        extends CassandraRepository<CassandraPrivateChatByFirstUserEntity, CassandraPrivateChatByFirstUserKey> {

    @Query("select * from private_chats_by_first_user where first_user_id = :firstUserId")
    List<CassandraPrivateChatByFirstUserEntity> getAllByFirstUserId(
            @Param("firstUserId") UUID firstUserId
    );

}
