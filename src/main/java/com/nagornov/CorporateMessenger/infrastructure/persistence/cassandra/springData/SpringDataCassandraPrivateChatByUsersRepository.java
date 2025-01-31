package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByUsersEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraPrivateChatByUsersKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCassandraPrivateChatByUsersRepository
        extends CassandraRepository<CassandraPrivateChatByUsersEntity, CassandraPrivateChatByUsersKey> {


    @Query("select * from private_chats_by_users where first_user_id = :firstUserId and second_user_id = :secondUserId")
    List<CassandraPrivateChatByUsersEntity> getAllByFirstUserIdAndSecondUserId(
            @Param("firstUserId") UUID firstUserId, @Param("secondUserId") UUID secondUserId
    );
}
