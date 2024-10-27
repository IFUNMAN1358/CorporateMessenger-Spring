package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.entity.AuthCassandraUserEntity;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.mapper.AuthCassandraUserMapper;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.springData.AuthSpringDataCassandraUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthCassandraUserRepository {

    private final AuthSpringDataCassandraUserRepository springDataCassandraUserRepository;
    private final AuthCassandraUserMapper cassandraUserMapper;

    public Optional<AuthUser> findUserById(UUID id) {
        return springDataCassandraUserRepository
                .findAuthCassandraUserEntityById(id)
                .map(cassandraUserMapper::toDomain);
    }

    public AuthUser save(AuthUser user) {
        AuthCassandraUserEntity entity = springDataCassandraUserRepository.save(
                cassandraUserMapper.toEntity(user)
        );
        return cassandraUserMapper.toDomain(entity);
    }

    public void delete(AuthUser user) {
        springDataCassandraUserRepository.delete(
                cassandraUserMapper.toEntity(user)
        );
    }
}
