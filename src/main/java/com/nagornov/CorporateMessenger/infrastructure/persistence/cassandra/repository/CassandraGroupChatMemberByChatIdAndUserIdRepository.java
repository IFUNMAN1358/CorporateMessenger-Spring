package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByChatIdAndUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatMemberMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatMemberByChatIdAndUserIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatMemberByChatIdAndUserIdRepository {

    private final SpringDataCassandraGroupChatMemberByChatIdAndUserIdRepository springDataCassandraGroupChatMemberByChatIdAndUserIdRepository;
    private final CassandraGroupChatMemberMapper cassandraGroupChatMemberMapper;

    public GroupChatMember save(GroupChatMember groupChatMember) {
        CassandraGroupChatMemberByChatIdAndUserIdEntity entity =
                springDataCassandraGroupChatMemberByChatIdAndUserIdRepository.save(
                        cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdAndUserIdEntity(groupChatMember)
                );
        return cassandraGroupChatMemberMapper.toDomain(entity);
    }

    public void delete(GroupChatMember groupChatMember) {
        springDataCassandraGroupChatMemberByChatIdAndUserIdRepository.delete(
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdAndUserIdEntity(groupChatMember)
        );
    }

    public Optional<GroupChatMember> findByChatIdAndUserId(UUID chatId, UUID userId) {
        return springDataCassandraGroupChatMemberByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .map(cassandraGroupChatMemberMapper::toDomain);
    }
}
