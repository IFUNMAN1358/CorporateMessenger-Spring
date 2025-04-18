package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatPhotoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraGroupChatPhotoDomainService {

    private final CassandraGroupChatPhotoRepository cassandraGroupChatPhotoRepository;

    public ChatPhoto save(@NonNull ChatPhoto chatPhoto) {
        return cassandraGroupChatPhotoRepository.save(chatPhoto);
    }

    public void delete(@NonNull ChatPhoto chatPhoto) {
        cassandraGroupChatPhotoRepository.delete(chatPhoto);
    }

    public List<ChatPhoto> findAllByChatId(@NonNull UUID chatId) {
        return cassandraGroupChatPhotoRepository.findAllByChatId(chatId);
    }

    public Optional<ChatPhoto> findFirstByChatId(@NonNull UUID chatId) {
        List<ChatPhoto> photos = cassandraGroupChatPhotoRepository.findAllByChatId(chatId);
        if (photos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(photos.getFirst());
    }

    public ChatPhoto getFirstByChatId(@NonNull UUID chatId) {
        List<ChatPhoto> photos = cassandraGroupChatPhotoRepository.findAllByChatId(chatId);
        if (photos.isEmpty()) {
            throw new ResourceNotFoundException("GroupChatPhoto[chatId=%s] not found".formatted(chatId));
        }
        return photos.getFirst();
    }

}
