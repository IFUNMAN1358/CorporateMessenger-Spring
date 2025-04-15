package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraGroupChatDomainService {

    private final CassandraGroupChatRepository cassandraGroupChatRepository;

    public GroupChat save(@NonNull GroupChat groupChat) {
        return cassandraGroupChatRepository.save(groupChat);
    }

    public GroupChat getById(@NonNull UUID id) {
        return cassandraGroupChatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat with this id does not exist"));
    }

    public Optional<GroupChat> findById(@NonNull UUID id) {
        return cassandraGroupChatRepository.findById(id);
    }

}
