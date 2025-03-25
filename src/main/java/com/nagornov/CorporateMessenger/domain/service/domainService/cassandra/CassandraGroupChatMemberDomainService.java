package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatMemberByChatIdAndUserIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatMemberByChatIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatMemberByUserIdRepository;
import lombok.NonNull;
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

    public GroupChatMember save(@NonNull GroupChatMember groupChatMember) {
        cassandraGroupChatMemberByChatIdRepository.save(groupChatMember);
        cassandraGroupChatMemberByUserIdRepository.save(groupChatMember);
        return cassandraGroupChatMemberByChatIdAndUserIdRepository.save(groupChatMember);
    }

    public void delete(@NonNull GroupChatMember groupChatMember) {
        cassandraGroupChatMemberByChatIdRepository.delete(groupChatMember);
        cassandraGroupChatMemberByUserIdRepository.delete(groupChatMember);
        cassandraGroupChatMemberByChatIdAndUserIdRepository.delete(groupChatMember);
    }

    public GroupChatMember getByChatIdAndUserId(@NonNull UUID chatId, @NonNull UUID userId) {
        return cassandraGroupChatMemberByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat member with this chatId and userId is not found"));
    }

    public Optional<GroupChatMember> findByChatIdAndUserId(@NonNull UUID chatId, @NonNull UUID userId) {
        return cassandraGroupChatMemberByChatIdAndUserIdRepository.findByChatIdAndUserId(chatId, userId);
    }

    public List<GroupChatMember> getAllByChatId(@NonNull UUID chatId) {
        return cassandraGroupChatMemberByChatIdRepository.getAllByChatId(chatId);
    }

    public List<GroupChatMember> getAllByUserId(@NonNull UUID userId) {
        return cassandraGroupChatMemberByUserIdRepository.getAllByUserId(userId);
    }

    public void validateUserOwnership(@NonNull UUID chatId, @NonNull UUID userId) {
        cassandraGroupChatMemberByChatIdAndUserIdRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat member with this chatId and userId is not found"));
    }

}
