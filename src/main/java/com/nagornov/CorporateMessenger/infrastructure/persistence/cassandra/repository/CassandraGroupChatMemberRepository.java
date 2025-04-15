package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatMemberMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatMemberByChatIdAndUserIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatMemberByChatIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatMemberByUserIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatMemberRepository {

    private final SpringDataCassandraGroupChatMemberByChatIdAndUserIdRepository springDataCassandraGroupChatMemberByChatIdAndUserIdRepository;
    private final SpringDataCassandraGroupChatMemberByChatIdRepository springDataCassandraGroupChatMemberByChatIdRepository;
    private final SpringDataCassandraGroupChatMemberByUserIdRepository springDataCassandraGroupChatMemberByUserIdRepository;
    private final CassandraGroupChatMemberMapper cassandraGroupChatMemberMapper;


    public GroupChatMember save(GroupChatMember groupChatMember) {
        springDataCassandraGroupChatMemberByChatIdAndUserIdRepository.save(
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdAndUserIdEntity(groupChatMember)
        );
        springDataCassandraGroupChatMemberByChatIdRepository.save(
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdEntity(groupChatMember)
        );
        CassandraGroupChatMemberByUserIdEntity entity = springDataCassandraGroupChatMemberByUserIdRepository.save(
                cassandraGroupChatMemberMapper.toGroupChatMemberByUserIdEntity(groupChatMember)
        );
        return cassandraGroupChatMemberMapper.toDomain(entity);
    }

    public void delete(GroupChatMember groupChatMember) {
        springDataCassandraGroupChatMemberByChatIdAndUserIdRepository.delete(
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdAndUserIdEntity(groupChatMember)
        );
        springDataCassandraGroupChatMemberByChatIdRepository.delete(
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdEntity(groupChatMember)
        );
        springDataCassandraGroupChatMemberByUserIdRepository.delete(
                cassandraGroupChatMemberMapper.toGroupChatMemberByUserIdEntity(groupChatMember)
        );
    }

    public Optional<GroupChatMember> findByChatIdAndUserId(UUID chatId, UUID userId) {
        return springDataCassandraGroupChatMemberByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .map(cassandraGroupChatMemberMapper::toDomain);
    }

    public List<GroupChatMember> getAllByChatId(UUID chatId) {
        return springDataCassandraGroupChatMemberByChatIdRepository
                .getAllByChatId(chatId)
                .stream()
                .map(cassandraGroupChatMemberMapper::toDomain)
                .toList();
    }

    public List<GroupChatMember> getAllByUserId(UUID userId) {
        return springDataCassandraGroupChatMemberByUserIdRepository
               .getAllByUserId(userId)
               .stream()
               .map(cassandraGroupChatMemberMapper::toDomain)
               .toList();
    }

}
