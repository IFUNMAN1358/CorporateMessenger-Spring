package com.nagornov.CorporateMessenger.domain.service.chat;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatMemberStatus;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatMemberRepository;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

    private final CassandraChatMemberRepository cassandraChatMemberRepository;

    public List<ChatMember> createAll(@NonNull Long chatId, @NonNull List<UUID> userIds, @NonNull ChatMemberStatus status) {
        List<ChatMember> chatMembers = new ArrayList<>();
        for (UUID userId : userIds) {
            chatMembers.add(
                    new ChatMember(chatId, userId, status, Instant.now(), Instant.now())
            );
        }
        return cassandraChatMemberRepository.saveAll(chatMembers);
    }

    public ChatMember create(@NonNull Long chatId, @NonNull UUID userId, @NonNull ChatMemberStatus status) {
        ChatMember chatMember = new ChatMember(chatId, userId, status, Instant.now(), Instant.now());
        return cassandraChatMemberRepository.save(chatMember);
    }

    public ChatMember update(@NonNull ChatMember chatMember) {
        return cassandraChatMemberRepository.save(chatMember);
    }

    public void delete(@NonNull ChatMember chatMember) {
        cassandraChatMemberRepository.delete(chatMember);
    }

    public void deleteAllByChatIdAndUserIds(@NonNull Long chatId, @NonNull List<UUID> userIds) {
        cassandraChatMemberRepository.deleteAllByChatIdAndUserIds(chatId, userIds);
    }

    public List<ChatMember> findAllByChatId(@NonNull Long chatId) {
        return cassandraChatMemberRepository.findAllByChatId(chatId);
    }

    public List<ChatMember> findAllByChatId(@NonNull Long chatId, int page, int size) {
        return cassandraChatMemberRepository.findAllByChatId(chatId, page, size);
    }

    public List<ChatMember> findAllByUserId(@NonNull UUID userId) {
        return cassandraChatMemberRepository.findAllByUserId(userId);
    }

    public boolean existsByChatIdAndUserId(@NonNull Long chatId, @NotNull UUID userId) {
        return cassandraChatMemberRepository.existsByChatIdAndUserId(chatId, userId);
    }

    public void ensureExistsByChatIdAndUserId(@NonNull Long chatId, @NotNull UUID userId) {
        if (!cassandraChatMemberRepository.existsByChatIdAndUserId(chatId, userId)) {
            throw new ResourceNotFoundException("ChatMember[chatId=%s, userId=%s]".formatted(chatId, userId));
        }
    }

    public ChatMember getByChatIdAndUserId(@NonNull Long chatId, @NonNull UUID userId) {
        return cassandraChatMemberRepository.findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMember[chatId=%s, userId=%s]".formatted(chatId, userId)));
    }

}
