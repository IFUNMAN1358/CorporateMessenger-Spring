package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatPhotoByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraChatPhotoMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraChatPhotoByChatIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraChatPhotoRepository {

    private final SpringDataCassandraChatPhotoByChatIdRepository springDataCassandraChatPhotoByChatIdRepository;
    private final CassandraChatPhotoMapper cassandraChatPhotoMapper;

    public ChatPhoto save(ChatPhoto chatPhoto) {
        CassandraChatPhotoByChatIdEntity entity =
                springDataCassandraChatPhotoByChatIdRepository.save(
                        cassandraChatPhotoMapper.toChatPhotoByChatIdEntity(chatPhoto)
                );
        return cassandraChatPhotoMapper.toDomain(entity);
    }

    public void delete(ChatPhoto chatPhoto) {
        springDataCassandraChatPhotoByChatIdRepository.delete(
                cassandraChatPhotoMapper.toChatPhotoByChatIdEntity(chatPhoto)
        );
    }

    public Optional<ChatPhoto> findByChatId(Long chatId) {
        return springDataCassandraChatPhotoByChatIdRepository.findByChatId(chatId)
                .map(cassandraChatPhotoMapper::toDomain);
    }

    public Optional<ChatPhoto> findByIdAndChatId(UUID id, Long chatId) {
        return springDataCassandraChatPhotoByChatIdRepository.findByIdAndChatId(id, chatId)
                .map(cassandraChatPhotoMapper::toDomain);
    }

}
