package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatMemberByUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraChatMemberByUserIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCassandraChatMemberByUserIdRepository
        extends CassandraRepository<CassandraChatMemberByUserIdEntity, CassandraChatMemberByUserIdKey> {

    @Query("SELECT * FROM chat_members_by_user_id WHERE user_id = :userId")
    Optional<CassandraChatMemberByUserIdEntity> findByUserId(@Param("userId") UUID userId);

}
