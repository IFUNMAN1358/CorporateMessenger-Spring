package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByChatIdAndUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatMemberMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatMemberByChatIdAndUserIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatMemberByChatIdAndUserIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraGroupChatMemberByChatIdAndUserIdRepository
            springDataCassandraGroupChatMemberByChatIdAndUserIdRepository;
    private final CassandraGroupChatMemberMapper cassandraGroupChatMemberMapper;

    public void saveWithoutCheck(@NotNull GroupChatMember groupChatMember) {
        final CassandraGroupChatMemberByChatIdAndUserIdEntity entity =
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdAndUserIdEntity(groupChatMember);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull GroupChatMember groupChatMember) {
        final CassandraGroupChatMemberByChatIdAndUserIdEntity entity =
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdAndUserIdEntity(groupChatMember);
        cassandraTemplate.update(entity);
    }

    public void deleteWithoutCheck(@NotNull GroupChatMember groupChatMember) {
        final CassandraGroupChatMemberByChatIdAndUserIdEntity entity =
                cassandraGroupChatMemberMapper.toGroupChatMemberByChatIdAndUserIdEntity(groupChatMember);
        cassandraTemplate.delete(entity);
    }

    public Optional<GroupChatMember> findByChatIdAndUserId(@NotNull UUID chatId, @NotNull UUID userId) {
        return springDataCassandraGroupChatMemberByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .map(cassandraGroupChatMemberMapper::toDomain);
    }
}
