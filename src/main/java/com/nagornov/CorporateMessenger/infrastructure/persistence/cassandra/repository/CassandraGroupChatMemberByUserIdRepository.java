package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatMemberMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatMemberByUserIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatMemberByUserIdRepository {

    private final CassandraTemplate cassandraTemplate;
    private final SpringDataCassandraGroupChatMemberByUserIdRepository
            springDataCassandraGroupChatMemberByUserIdRepository;
    private final CassandraGroupChatMemberMapper cassandraGroupChatMemberMapper;

    public void saveWithoutCheck(@NotNull GroupChatMember groupChatMember) {
        final CassandraGroupChatMemberByUserIdEntity entity =
                cassandraGroupChatMemberMapper.toGroupChatMemberByUserIdEntity(groupChatMember);
        cassandraTemplate.insert(entity);
    }

    public void updateWithoutCheck(@NotNull GroupChatMember groupChatMember) {
        final CassandraGroupChatMemberByUserIdEntity entity =
                cassandraGroupChatMemberMapper.toGroupChatMemberByUserIdEntity(groupChatMember);
        cassandraTemplate.update(entity);
    }

    public void deleteWithoutCheck(@NotNull GroupChatMember groupChatMember) {
        final CassandraGroupChatMemberByUserIdEntity entity =
                cassandraGroupChatMemberMapper.toGroupChatMemberByUserIdEntity(groupChatMember);
        cassandraTemplate.delete(entity);
    }

    public List<GroupChatMember> getAllByUserId(@NotNull UUID userId) {
        return springDataCassandraGroupChatMemberByUserIdRepository
               .getAllByUserId(userId)
               .stream()
               .map(cassandraGroupChatMemberMapper::toDomain)
               .toList();
    }

}
