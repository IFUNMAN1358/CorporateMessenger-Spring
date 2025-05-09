package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageFileByMessageIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraMessageFileByMessageIdKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCassandraMessageFileByMessageIdRepository
        extends CassandraRepository<CassandraMessageFileByMessageIdEntity, CassandraMessageFileByMessageIdKey> {

    @Query("select * from message_files_by_message_id where message_id = :messageId")
    List<CassandraMessageFileByMessageIdEntity> findAllByMessageId(@Param("messageId") UUID messageId);

    @Query("select * from message_files_by_message_id where id = :id and message_id = :messageId")
    Optional<CassandraMessageFileByMessageIdEntity> findByIdAndMessageId(
            @Param("id") UUID id, @Param("messageId") UUID messageId
    );

    @Query("DELETE FROM message_files_by_message_id WHERE message_id = :messageId")
    void deleteAllByMessageId(@Param("messageId") UUID messageId);

}
