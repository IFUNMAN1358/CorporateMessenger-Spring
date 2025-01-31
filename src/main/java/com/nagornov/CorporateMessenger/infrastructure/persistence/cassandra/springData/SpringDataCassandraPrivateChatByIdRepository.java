package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByIdEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCassandraPrivateChatByIdRepository extends CassandraRepository<CassandraPrivateChatByIdEntity, UUID> {

    @Query("select * from private_chats_by_id where id = :id")
    Optional<CassandraPrivateChatByIdEntity> findCassandraPrivateChatEntityById(@Param("id") UUID id);

}