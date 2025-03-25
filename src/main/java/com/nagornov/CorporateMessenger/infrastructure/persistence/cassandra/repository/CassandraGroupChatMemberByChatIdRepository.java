package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatMemberMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatMemberByChatIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatMemberByChatIdRepository {

    private final SpringDataCassandraGroupChatMemberByChatIdRepository springDataCassandraGroupChatMemberByChatIdRepository;
    private final CassandraGroupChatMemberMapper cassandraGroupChatMemberMapper;

    public GroupChatMember save(GroupChatMember groupChatMember) {
        CassandraGroupChatMemberByChatIdEntity entity =
                springDataCassandraGroupChatMemberByChatIdRepository.save(
                        cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdEntity(groupChatMember)
                );
        return cassandraGroupChatMemberMapper.toDomain(entity);
    }

    public void delete(GroupChatMember groupChatMember) {
        springDataCassandraGroupChatMemberByChatIdRepository.delete(
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdEntity(groupChatMember)
        );
    }

    public List<GroupChatMember> getAllByChatId( UUID chatId) {
        return springDataCassandraGroupChatMemberByChatIdRepository
                .getAllByChatId(chatId)
                .stream()
                .map(cassandraGroupChatMemberMapper::toDomain)
                .toList();
    }
}
