package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByFirstUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByFirstUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatByFirstUserRepository {

    private final SpringDataCassandraPrivateChatByFirstUserRepository springDataCassandraPrivateChatByFirstUserRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;

    public PrivateChat save(PrivateChat privateChat) {
        CassandraPrivateChatByFirstUserEntity entity =
                springDataCassandraPrivateChatByFirstUserRepository.save(
                        cassandraPrivateChatMapper.toPrivateChatByFirstUserEntity(privateChat)
                );
        return cassandraPrivateChatMapper.toDomain(entity);
    }

    public List<PrivateChat> getAllByFirstUserId(UUID firstUserId) {
        return springDataCassandraPrivateChatByFirstUserRepository
                .getAllByFirstUserId(firstUserId)
                .stream().map(cassandraPrivateChatMapper::toDomain).toList();
    }
}
