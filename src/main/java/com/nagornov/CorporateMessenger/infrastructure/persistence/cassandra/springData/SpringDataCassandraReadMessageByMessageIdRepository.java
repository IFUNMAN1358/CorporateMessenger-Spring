package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraReadMessageByMessageIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraReadMessageByMessageIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCassandraReadMessageByMessageIdRepository
        extends CassandraRepository<CassandraReadMessageByMessageIdEntity, CassandraReadMessageByMessageIdKey> {

    @Query("select * from read_messages_by_message_id where message_id = :messageId")
    List<CassandraReadMessageByMessageIdEntity> findAllByMessageId(@Param("messageId") UUID messageId);

    @Query("DELETE FROM read_messages_by_message_id WHERE message_id = :messageId")
    void deleteAllByMessageId(@Param("messageId") UUID messageId);
}
