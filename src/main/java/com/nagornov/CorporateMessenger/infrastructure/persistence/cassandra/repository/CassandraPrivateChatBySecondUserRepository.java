package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatBySecondUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatBySecondUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatBySecondUserRepository {

    private final SpringDataCassandraPrivateChatBySecondUserRepository springDataCassandraPrivateChatBySecondUserRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;

    public PrivateChat save(PrivateChat privateChat) {
        CassandraPrivateChatBySecondUserEntity entity =
                springDataCassandraPrivateChatBySecondUserRepository.save(
                        cassandraPrivateChatMapper.toPrivateChatBySecondUserEntity(privateChat)
                );
        return cassandraPrivateChatMapper.toDomain(entity);
    }

    public List<PrivateChat> getAllBySecondUserId(UUID secondUserId) {
        return springDataCassandraPrivateChatBySecondUserRepository
                .getAllBySecondUserId(secondUserId)
                .stream().map(cassandraPrivateChatMapper::toDomain).toList();
    }
}
