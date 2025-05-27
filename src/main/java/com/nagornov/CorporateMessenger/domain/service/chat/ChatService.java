package com.nagornov.CorporateMessenger.domain.service.chat;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatType;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final CassandraChatRepository cassandraChatRepository;

    public Chat createGroup(@NonNull String title, @NonNull String username) {
        Chat chat = new Chat(
                Chat.generateId(),
                ChatType.GROUP,
                username,
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
                ChatType.PRIVATE,
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

    public Boolean existsByUsername(@NonNull String username) {
        return cassandraChatRepository.existsByUsername(username);
    }

    public Chat getById(@NonNull Long id) {
        return cassandraChatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat[id=%s] not found".formatted(id)));
    }

    public Chat getByUsername(@NonNull String username) {
        return cassandraChatRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Chat[username=%s] not found".formatted(username)));
    }

    public void ensureNotExistsByUsername(@NonNull String username) {
        if (cassandraChatRepository.existsByUsername(username)) {
            throw new ResourceConflictException(
                    "Chat[username=%s] already exists".formatted(username),
                    new FieldError("username", "Чат с таким уникальным именем уже существует")
            );
        }
    }

}
