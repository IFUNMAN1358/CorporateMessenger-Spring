package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatByUsernameEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraChatByUsernameKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataCassandraChatByUsernameRepository
        extends CassandraRepository<CassandraChatByUsernameEntity, CassandraChatByUsernameKey> {

    @Query("SELECT * FROM chats_by_username WHERE username = :username")
    Optional<CassandraChatByUsernameEntity> findByUsername(@Param("username") String username);

}
