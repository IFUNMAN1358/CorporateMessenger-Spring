package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByFirstUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByFirstUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatByFirstUserRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraPrivateChatByFirstUserRepository springDataCassandraPrivateChatByFirstUserRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;

    public void saveWithoutCheck(@NotNull PrivateChat privateChat) {
        final CassandraPrivateChatByFirstUserEntity entity =
                cassandraPrivateChatMapper.toPrivateChatByFirstUserEntity(privateChat);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull PrivateChat privateChat) {
        final CassandraPrivateChatByFirstUserEntity entity =
                cassandraPrivateChatMapper.toPrivateChatByFirstUserEntity(privateChat);
        cassandraTemplate.update(entity);
    }

    public void deleteByIdWithoutCheck(@NotNull UUID id) {
        cassandraTemplate.deleteById(id, CassandraPrivateChatByFirstUserEntity.class);
    }

    public List<PrivateChat> getAllByFirstUserId(@NotNull UUID firstUserId) {
        return springDataCassandraPrivateChatByFirstUserRepository
                .getAllByFirstUserId(firstUserId)
                .stream().map(cassandraPrivateChatMapper::toDomain).toList();
    }
}
