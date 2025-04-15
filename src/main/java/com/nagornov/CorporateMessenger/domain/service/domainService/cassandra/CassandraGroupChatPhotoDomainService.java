package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatPhoto;
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

    public GroupChatPhoto save(@NonNull GroupChatPhoto groupChatPhoto) {
        return cassandraGroupChatPhotoRepository.save(groupChatPhoto);
    }

    public void delete(@NonNull GroupChatPhoto groupChatPhoto) {
        cassandraGroupChatPhotoRepository.delete(groupChatPhoto);
    }

    public List<GroupChatPhoto> findAllByChatId(@NonNull UUID chatId) {
        return cassandraGroupChatPhotoRepository.findAllByChatId(chatId);
    }

    public Optional<GroupChatPhoto> findFirstByChatId(@NonNull UUID chatId) {
        List<GroupChatPhoto> photos = cassandraGroupChatPhotoRepository.findAllByChatId(chatId);
        if (photos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(photos.getFirst());
    }

    public GroupChatPhoto getFirstByChatId(@NonNull UUID chatId) {
        List<GroupChatPhoto> photos = cassandraGroupChatPhotoRepository.findAllByChatId(chatId);
        if (photos.isEmpty()) {
            throw new ResourceNotFoundException("GroupChatPhoto[chatId=%s] not found".formatted(chatId));
        }
        return photos.getFirst();
    }

}
