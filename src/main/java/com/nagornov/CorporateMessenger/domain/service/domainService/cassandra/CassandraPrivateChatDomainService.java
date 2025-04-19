package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraPrivateChatRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CassandraPrivateChatDomainService {

    private final CassandraPrivateChatRepository cassandraPrivateChatRepository;

    public PrivateChat save(@NonNull PrivateChat privateChat) {
        return cassandraPrivateChatRepository.save(privateChat);
    }

    public void delete(@NonNull PrivateChat privateChat) {
        cassandraPrivateChatRepository.delete(privateChat);
    }

    public Optional<PrivateChat> findByUserPairHash(@NonNull String userPairHash) {
        return cassandraPrivateChatRepository.findByUserPairHash(userPairHash);
    }

    public PrivateChat getByUserPairHash(@NonNull String userPairHash) {
        return cassandraPrivateChatRepository.findByUserPairHash(userPairHash)
                .orElseThrow(() -> new ResourceNotFoundException("PrivateChat[userPairHash=%s]".formatted(userPairHash)));
    }

}
