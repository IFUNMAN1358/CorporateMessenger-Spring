package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByUsersEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatByUsersRepository {

    private final SpringDataCassandraPrivateChatByUsersRepository springDataCassandraPrivateChatByUsersRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;

    public PrivateChat save(PrivateChat privateChat) {
        CassandraPrivateChatByUsersEntity entity =
                springDataCassandraPrivateChatByUsersRepository.save(
                        cassandraPrivateChatMapper.toPrivateChatByUsersEntity(privateChat)
                );
        return cassandraPrivateChatMapper.toDomain(entity);
    }

    public List<PrivateChat> getAllByFirstUserIdAndSecondUserId(UUID firstUserId, UUID secondUserId) {
        return springDataCassandraPrivateChatByUsersRepository
                .getAllByFirstUserIdAndSecondUserId(firstUserId, secondUserId)
                .stream().map(cassandraPrivateChatMapper::toDomain).toList();
    }
}
