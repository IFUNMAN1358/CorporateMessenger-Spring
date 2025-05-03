package com.nagornov.CorporateMessenger.domain.service.chat;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatType;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final CassandraChatRepository cassandraChatRepository;

    public Chat createGroup(@NonNull String title) {
        Chat chat = new Chat(
                Chat.generateId(),
                ChatType.PRIVATE.getType(),
                null,
                title,
                null,
                "URL",
                false,
                false,
                Instant.now(),
                Instant.now()
        );
        return cassandraChatRepository.save(chat);
    }

    public Chat createPrivate() {
        Chat chat = new Chat(
                Chat.generateId(),
                ChatType.PRIVATE.getType(),
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );
        return cassandraChatRepository.save(chat);
    }

    public Chat update(@NonNull Chat chat) {
        return cassandraChatRepository.save(chat);
    }

    public void delete(@NonNull Chat chat) {
        cassandraChatRepository.delete(chat);
    }

    public Chat getById(@NonNull Long id) {
        return cassandraChatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat[id=%s] not found".formatted(id)));
    }

    public Chat getByUsername(@NonNull String username) {
        return cassandraChatRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Chat[username=%s] not found".formatted(username)));
    }

}
