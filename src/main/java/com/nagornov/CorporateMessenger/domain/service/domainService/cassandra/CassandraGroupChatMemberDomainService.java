package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
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

    public ChatMember save(@NonNull ChatMember chatMember) {
        return cassandraGroupChatMemberRepository.save(chatMember);
    }

    public void delete(@NonNull ChatMember chatMember) {
        cassandraGroupChatMemberRepository.delete(chatMember);
    }

    public ChatMember getByChatIdAndUserId(@NonNull UUID chatId, @NonNull UUID userId) {
        return cassandraGroupChatMemberRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat member with this chatId and userId is not found"));
    }

    public Optional<ChatMember> findByChatIdAndUserId(@NonNull UUID chatId, @NonNull UUID userId) {
        return cassandraGroupChatMemberRepository.findByChatIdAndUserId(chatId, userId);
    }

    public List<ChatMember> getAllByChatId(@NonNull UUID chatId) {
        return cassandraGroupChatMemberRepository.getAllByChatId(chatId);
    }

    public List<ChatMember> getAllByUserId(@NonNull UUID userId) {
        return cassandraGroupChatMemberRepository.getAllByUserId(userId);
    }

    public void validateUserOwnership(@NonNull UUID chatId, @NonNull UUID userId) {
        cassandraGroupChatMemberRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat member with this chatId and userId is not found"));
    }

}
