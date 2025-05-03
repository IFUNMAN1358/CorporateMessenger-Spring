package com.nagornov.CorporateMessenger.domain.service.chat;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatMemberStatus;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatMemberRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

    private final CassandraChatMemberRepository cassandraChatMemberRepository;

    public ChatMember create(@NonNull Long chatId, @NonNull UUID userId, @NonNull ChatMemberStatus memberStatus) {
        ChatMember userChatMember = new ChatMember(
                chatId,
                userId,
                memberStatus.getStatus(),
                Instant.now(),
                Instant.now()
        );
        return cassandraChatMemberRepository.save(userChatMember);
    }

    public ChatMember update(@NonNull ChatMember chatMember) {
        return cassandraChatMemberRepository.save(chatMember);
    }

    public void delete(@NonNull ChatMember chatMember) {
        cassandraChatMemberRepository.delete(chatMember);
    }

    public List<ChatMember> findAllByChatId(@NonNull Long chatId) {
        return cassandraChatMemberRepository.findAllByChatId(chatId);
    }

    public List<ChatMember> findAllByUserId(@NonNull UUID userId) {
        return cassandraChatMemberRepository.findAllByUserId(userId);
    }

    public ChatMember getByChatIdAndUserId(@NonNull Long chatId, @NonNull UUID userId) {
        return cassandraChatMemberRepository.findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMember[chatId=%s, userId=%s]".formatted(chatId, userId)));
    }

}
