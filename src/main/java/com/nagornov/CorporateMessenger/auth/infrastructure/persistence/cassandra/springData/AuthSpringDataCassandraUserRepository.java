package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.springData;

import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.entity.AuthCassandraUserEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthSpringDataCassandraUserRepository extends CassandraRepository<AuthCassandraUserEntity, UUID> {

    @Query("select * from users where id = :id")
    Optional<AuthCassandraUserEntity> findAuthCassandraUserEntityById(@Param("id") UUID id);

}
