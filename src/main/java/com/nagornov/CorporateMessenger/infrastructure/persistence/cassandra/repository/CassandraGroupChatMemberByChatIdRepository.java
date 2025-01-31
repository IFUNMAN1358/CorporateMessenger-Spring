package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatMemberMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatMemberByChatIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatMemberByChatIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraGroupChatMemberByChatIdRepository
            springDataCassandraGroupChatMemberByChatIdRepository;
    private final CassandraGroupChatMemberMapper cassandraGroupChatMemberMapper;

    public void saveWithoutCheck(@NotNull GroupChatMember groupChatMember) {
        final CassandraGroupChatMemberByChatIdEntity entity =
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdEntity(groupChatMember);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull GroupChatMember groupChatMember) {
        final CassandraGroupChatMemberByChatIdEntity entity =
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdEntity(groupChatMember);
        cassandraTemplate.update(entity);
    }

    public void deleteWithoutCheck(@NotNull GroupChatMember groupChatMember) {
        final CassandraGroupChatMemberByChatIdEntity entity =
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdEntity(groupChatMember);
        cassandraTemplate.delete(entity);
    }

    public List<GroupChatMember> getAllByChatId(@NotNull UUID chatId) {
        return springDataCassandraGroupChatMemberByChatIdRepository
                .getAllByChatId(chatId)
                .stream()
                .map(cassandraGroupChatMemberMapper::toDomain)
                .toList();
    }
}
