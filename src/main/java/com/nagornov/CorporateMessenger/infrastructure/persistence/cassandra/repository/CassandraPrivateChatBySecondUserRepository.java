package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatBySecondUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatBySecondUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatBySecondUserRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraPrivateChatBySecondUserRepository springDataCassandraPrivateChatBySecondUserRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;

    public void saveWithoutCheck(@NotNull PrivateChat privateChat) {
        final CassandraPrivateChatBySecondUserEntity entity =
                cassandraPrivateChatMapper.toPrivateChatBySecondUserEntity(privateChat);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull PrivateChat privateChat) {
        final CassandraPrivateChatBySecondUserEntity entity =
                cassandraPrivateChatMapper.toPrivateChatBySecondUserEntity(privateChat);
        cassandraTemplate.update(entity);
    }

    public void deleteByIdWithoutCheck(@NotNull UUID id) {
        cassandraTemplate.deleteById(id, CassandraPrivateChatBySecondUserEntity.class);
    }

    public List<PrivateChat> getAllBySecondUserId(@NotNull UUID secondUserId) {
        return springDataCassandraPrivateChatBySecondUserRepository
                .getAllBySecondUserId(secondUserId)
                .stream().map(cassandraPrivateChatMapper::toDomain).toList();
    }
}
