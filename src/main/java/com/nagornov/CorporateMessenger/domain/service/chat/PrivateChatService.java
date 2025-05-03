package com.nagornov.CorporateMessenger.domain.service.chat;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraPrivateChatRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrivateChatService {

    private final CassandraPrivateChatRepository cassandraPrivateChatRepository;

    public PrivateChat create(@NonNull String userPairHash, @NonNull Long chatId) {
        PrivateChat privateChat = new PrivateChat(userPairHash, chatId);
        return cassandraPrivateChatRepository.save(privateChat);
    }

    public PrivateChat update(@NonNull PrivateChat privateChat) {
        return cassandraPrivateChatRepository.save(privateChat);
    }

    public void delete(@NonNull PrivateChat privateChat) {
        cassandraPrivateChatRepository.delete(privateChat);
    }

    public void deleteByUserPairHash(@NonNull String userPairHash) {
        cassandraPrivateChatRepository.deleteByUserPairHash(userPairHash);
    }

    public Optional<PrivateChat> findByUserPairHash(@NonNull String userPairHash) {
        return cassandraPrivateChatRepository.findByUserPairHash(userPairHash);
    }

    public PrivateChat getByUserPairHash(@NonNull String userPairHash) {
        return cassandraPrivateChatRepository.findByUserPairHash(userPairHash)
                .orElseThrow(() -> new ResourceNotFoundException("PrivateChat[userPairHash=%s]".formatted(userPairHash)));
    }

}
