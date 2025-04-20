package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByUserPairHashEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataCassandraPrivateChatByUserPairHashRepository
        extends CassandraRepository<CassandraPrivateChatByUserPairHashEntity, String> {

    @Query("SELECT * FROM private_chats_by_user_pair_hash WHERE user_pair_hash = :userPairHash")
    Optional<CassandraPrivateChatByUserPairHashEntity> findByUserPairHash(@Param("userPairHash") String userPairHash);

    @Query("DELETE FROM private_chats_by_user_pair_hash WHERE user_pair_hash = :userPairHash")
    void deleteByUserPairHash(@Param("userPairHash") String userPairHash);

}
