package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatMemberRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraChatMemberDomainService {

    private final CassandraChatMemberRepository cassandraChatMemberRepository;

    public ChatMember save(@NonNull ChatMember chatMember) {
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
