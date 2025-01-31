package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatBySecondUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraPrivateChatBySecondUserKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCassandraPrivateChatBySecondUserRepository
        extends CassandraRepository<CassandraPrivateChatBySecondUserEntity, CassandraPrivateChatBySecondUserKey> {

    @Query("select * from private_chats_by_second_user where second_user_id = :secondUserId")
    List<CassandraPrivateChatBySecondUserEntity> getAllBySecondUserId(
            @Param("secondUserId") UUID secondUserId
    );

}