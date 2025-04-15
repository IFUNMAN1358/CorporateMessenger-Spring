package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatRepository {

    private final SpringDataCassandraGroupChatByIdRepository springDataCassandraGroupChatByIdRepository;
    private final CassandraGroupChatMapper cassandraGroupChatMapper;

    public GroupChat save(GroupChat groupChat) {
        CassandraGroupChatByIdEntity entity =
                springDataCassandraGroupChatByIdRepository.save(
                        cassandraGroupChatMapper.toGroupChatByIdEntity(groupChat)
                );
        return cassandraGroupChatMapper.toDomain(entity);
    }

    public void delete(GroupChat groupChat) {
        springDataCassandraGroupChatByIdRepository.delete(
                cassandraGroupChatMapper.toGroupChatByIdEntity(groupChat)
        );
    }

    public Optional<GroupChat> findById(UUID id) {
        return springDataCassandraGroupChatByIdRepository
                .findById(id)
                .map(cassandraGroupChatMapper::toDomain);
    }
}
