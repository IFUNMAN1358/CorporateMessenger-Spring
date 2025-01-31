package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByUsersEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByUsersRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatByUsersRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraPrivateChatByUsersRepository springDataCassandraPrivateChatByUsersRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;


    public void saveWithoutCheck(@NotNull PrivateChat privateChat) {
        final CassandraPrivateChatByUsersEntity entity =
                cassandraPrivateChatMapper.toPrivateChatByUsersEntity(privateChat);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull PrivateChat privateChat) {
        final CassandraPrivateChatByUsersEntity entity =
                cassandraPrivateChatMapper.toPrivateChatByUsersEntity(privateChat);
        cassandraTemplate.update(entity);
    }

    public void deleteByIdWithoutCheck(@NotNull UUID id) {
        cassandraTemplate.deleteById(id, CassandraPrivateChatByUsersEntity.class);
    }

    public List<PrivateChat> getAllByFirstUserIdAndSecondUserId(@NotNull UUID firstUserId, @NotNull UUID secondUserId) {
        return springDataCassandraPrivateChatByUsersRepository
                .getAllByFirstUserIdAndSecondUserId(firstUserId, secondUserId)
                .stream().map(cassandraPrivateChatMapper::toDomain).toList();
    }
}
