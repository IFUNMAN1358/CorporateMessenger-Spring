package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatMemberByChatIdAndUserIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatMemberByChatIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatMemberByUserIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraGroupChatMemberDomainService {

    private final CassandraGroupChatMemberByChatIdRepository cassandraGroupChatMemberByChatIdRepository;
    private final CassandraGroupChatMemberByUserIdRepository cassandraGroupChatMemberByUserIdRepository;
    private final CassandraGroupChatMemberByChatIdAndUserIdRepository cassandraGroupChatMemberByChatIdAndUserIdRepository;

    public void save(@NotNull GroupChatMember groupChatMember) {
        cassandraGroupChatMemberByChatIdAndUserIdRepository
                .findByChatIdAndUserId(groupChatMember.getChatId(), groupChatMember.getUserId())
                .ifPresent(_ -> {
                    throw new ResourceConflictException("Group chat member already exists");
                });
        cassandraGroupChatMemberByChatIdRepository.saveWithoutCheck(groupChatMember);
        cassandraGroupChatMemberByUserIdRepository.saveWithoutCheck(groupChatMember);
        cassandraGroupChatMemberByChatIdAndUserIdRepository.saveWithoutCheck(groupChatMember);
    }

    public void update(@NotNull GroupChatMember groupChatMember) {
        cassandraGroupChatMemberByChatIdRepository.updateWithoutCheck(groupChatMember);
        cassandraGroupChatMemberByUserIdRepository.updateWithoutCheck(groupChatMember);
        cassandraGroupChatMemberByChatIdAndUserIdRepository.updateWithoutCheck(groupChatMember);
    }

    public void delete(@NotNull GroupChatMember groupChatMember) {
        cassandraGroupChatMemberByChatIdRepository.deleteWithoutCheck(groupChatMember);
        cassandraGroupChatMemberByUserIdRepository.deleteWithoutCheck(groupChatMember);
        cassandraGroupChatMemberByChatIdAndUserIdRepository.deleteWithoutCheck(groupChatMember);
    }

    public GroupChatMember getByChatIdAndUserId(@NotNull UUID chatId, @NotNull UUID userId) {
        return cassandraGroupChatMemberByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat member with this chatId and userId is not found"));
    }

    public Optional<GroupChatMember> findByChatIdAndUserId(@NotNull UUID chatId, @NotNull UUID userId) {
        return cassandraGroupChatMemberByChatIdAndUserIdRepository.findByChatIdAndUserId(chatId, userId);
    }

    public List<GroupChatMember> getAllByChatId(@NotNull UUID chatId) {
        return cassandraGroupChatMemberByChatIdRepository.getAllByChatId(chatId);
    }

    public List<GroupChatMember> getAllByUserId(@NotNull UUID userId) {
        return cassandraGroupChatMemberByUserIdRepository.getAllByUserId(userId);
    }

    public void validateUserOwnership(@NotNull UUID chatId, @NotNull UUID userId) {
        cassandraGroupChatMemberByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat member with this chatId and userId is not found"));
    }

}
