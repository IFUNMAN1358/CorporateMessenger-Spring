package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CassandraChatDomainService {

    private final CassandraChatRepository cassandraChatRepository;

    public Chat save(@NonNull Chat chat) {
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
