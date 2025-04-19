package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatPhotoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraChatPhotoDomainService {

    private final CassandraChatPhotoRepository cassandraChatPhotoRepository;

    public ChatPhoto save(@NonNull ChatPhoto chatPhoto) {
        return cassandraChatPhotoRepository.save(chatPhoto);
    }

    public void delete(@NonNull ChatPhoto chatPhoto) {
        cassandraChatPhotoRepository.delete(chatPhoto);
    }

    public List<ChatPhoto> findAllByChatId(@NonNull Long chatId) {
        return cassandraChatPhotoRepository.findAllByChatId(chatId);
    }

    public ChatPhoto getByIdAndChatId(@NonNull UUID id, @NonNull Long chatId) {
        return cassandraChatPhotoRepository.findByIdAndChatId(id, chatId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatPhoto[id=%s, chatId=%s] not found".formatted(id, chatId)));
    }

}
