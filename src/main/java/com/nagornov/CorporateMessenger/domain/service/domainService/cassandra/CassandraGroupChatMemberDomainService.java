package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatMemberRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraGroupChatMemberDomainService {

    private final CassandraGroupChatMemberRepository cassandraGroupChatMemberRepository;

    public GroupChatMember save(@NonNull GroupChatMember groupChatMember) {
        return cassandraGroupChatMemberRepository.save(groupChatMember);
    }

    public void delete(@NonNull GroupChatMember groupChatMember) {
        cassandraGroupChatMemberRepository.delete(groupChatMember);
    }

    public GroupChatMember getByChatIdAndUserId(@NonNull UUID chatId, @NonNull UUID userId) {
        return cassandraGroupChatMemberRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat member with this chatId and userId is not found"));
    }

    public Optional<GroupChatMember> findByChatIdAndUserId(@NonNull UUID chatId, @NonNull UUID userId) {
        return cassandraGroupChatMemberRepository.findByChatIdAndUserId(chatId, userId);
    }

    public List<GroupChatMember> getAllByChatId(@NonNull UUID chatId) {
        return cassandraGroupChatMemberRepository.getAllByChatId(chatId);
    }

    public List<GroupChatMember> getAllByUserId(@NonNull UUID userId) {
        return cassandraGroupChatMemberRepository.getAllByUserId(userId);
    }

    public void validateUserOwnership(@NonNull UUID chatId, @NonNull UUID userId) {
        cassandraGroupChatMemberRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat member with this chatId and userId is not found"));
    }

}
