package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatByIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatByIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraGroupChatByIdRepository springDataCassandraGroupChatByIdRepository;
    private final CassandraGroupChatMapper cassandraGroupChatMapper;

    public void saveWithoutCheck(@NotNull GroupChat groupChat) {
        final CassandraGroupChatByIdEntity entity =
                cassandraGroupChatMapper.toGroupChatByIdEntity(groupChat);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull GroupChat groupChat) {
        final CassandraGroupChatByIdEntity entity =
                cassandraGroupChatMapper.toGroupChatByIdEntity(groupChat);
        cassandraTemplate.update(entity);
    }

    public Optional<GroupChat> findById(@NotNull UUID id) {
        return springDataCassandraGroupChatByIdRepository
                .findById(id)
                .map(cassandraGroupChatMapper::toDomain);
    }

}
