package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraChatPhotoMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraChatPhotoByChatIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraChatPhotoRepository {

    private final SpringDataCassandraChatPhotoByChatIdRepository springDataCassandraChatPhotoByChatIdRepository;
    private final CassandraChatPhotoMapper cassandraChatPhotoMapper;

    public ChatPhoto save(ChatPhoto chatPhoto) {
        return cassandraChatPhotoMapper.toDomain(
                springDataCassandraChatPhotoByChatIdRepository.save(
                        cassandraChatPhotoMapper.toChatPhotoByChatIdEntity(chatPhoto)
                )
        );
    }

    public List<ChatPhoto> saveAll(List<ChatPhoto> chatPhotos) {
        return springDataCassandraChatPhotoByChatIdRepository.saveAll(
                chatPhotos.stream().map(cassandraChatPhotoMapper::toChatPhotoByChatIdEntity).toList()
        )
        .stream().map(cassandraChatPhotoMapper::toDomain).toList();
    }

    public void delete(ChatPhoto chatPhoto) {
        springDataCassandraChatPhotoByChatIdRepository.delete(
                cassandraChatPhotoMapper.toChatPhotoByChatIdEntity(chatPhoto)
        );
    }

    public List<ChatPhoto> findAllByChatId(Long chatId) {
        return springDataCassandraChatPhotoByChatIdRepository.findAllByChatId(chatId)
                .stream().map(cassandraChatPhotoMapper::toDomain).toList();
    }

    public Optional<ChatPhoto> findByIdAndChatId(UUID id, Long chatId) {
        return springDataCassandraChatPhotoByChatIdRepository.findByIdAndChatId(id, chatId)
                .map(cassandraChatPhotoMapper::toDomain);
    }

}
