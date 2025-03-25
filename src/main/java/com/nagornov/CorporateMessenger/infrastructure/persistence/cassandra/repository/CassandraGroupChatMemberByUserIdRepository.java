package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatMemberMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatMemberByUserIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatMemberByUserIdRepository {

    private final SpringDataCassandraGroupChatMemberByUserIdRepository springDataCassandraGroupChatMemberByUserIdRepository;
    private final CassandraGroupChatMemberMapper cassandraGroupChatMemberMapper;

    public GroupChatMember save(GroupChatMember groupChatMember) {
        CassandraGroupChatMemberByUserIdEntity entity =
                springDataCassandraGroupChatMemberByUserIdRepository.save(
                        cassandraGroupChatMemberMapper.toGroupChatMemberByUserIdEntity(groupChatMember)
                );
        return cassandraGroupChatMemberMapper.toDomain(entity);
    }

    public void delete(GroupChatMember groupChatMember) {
        springDataCassandraGroupChatMemberByUserIdRepository.delete(
                cassandraGroupChatMemberMapper.toGroupChatMemberByUserIdEntity(groupChatMember)
        );
    }

    public List<GroupChatMember> getAllByUserId(UUID userId) {
        return springDataCassandraGroupChatMemberByUserIdRepository
               .getAllByUserId(userId)
               .stream()
               .map(cassandraGroupChatMemberMapper::toDomain)
               .toList();
    }

}
