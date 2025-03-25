package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatByIdRepository {

    private final SpringDataCassandraPrivateChatByIdRepository springDataCassandraPrivateChatByIdRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;

    public PrivateChat save(PrivateChat privateChat) {
        CassandraPrivateChatByIdEntity entity =
                springDataCassandraPrivateChatByIdRepository.save(
                        cassandraPrivateChatMapper.toPrivateChatByIdEntity(privateChat)
                );
        return cassandraPrivateChatMapper.toDomain(entity);
    }

    public Optional<PrivateChat> findById(UUID id) {
        return springDataCassandraPrivateChatByIdRepository
                .findCassandraPrivateChatEntityById(id)
                .map(cassandraPrivateChatMapper::toDomain);
    }

}