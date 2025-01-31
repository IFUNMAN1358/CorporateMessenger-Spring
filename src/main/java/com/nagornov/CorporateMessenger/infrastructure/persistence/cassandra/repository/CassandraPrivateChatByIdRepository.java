package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatByIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraPrivateChatByIdRepository springDataCassandraPrivateChatByIdRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;

    public void saveWithoutCheck(@NotNull PrivateChat privateChat) {
        final CassandraPrivateChatByIdEntity entity =
                cassandraPrivateChatMapper.toPrivateChatByIdEntity(privateChat);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull PrivateChat privateChat) {
        final CassandraPrivateChatByIdEntity entity =
                cassandraPrivateChatMapper.toPrivateChatByIdEntity(privateChat);
        cassandraTemplate.update(entity);
    }

    public void deleteByIdWithoutCheck(@NotNull UUID id) {
        cassandraTemplate.deleteById(id, CassandraPrivateChatByIdEntity.class);
    }

    public Optional<PrivateChat> findById(@NotNull UUID id) {
        return springDataCassandraPrivateChatByIdRepository
                .findCassandraPrivateChatEntityById(id)
                .map(cassandraPrivateChatMapper::toDomain);
    }

}