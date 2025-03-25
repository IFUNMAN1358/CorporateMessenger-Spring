package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatByIdRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraGroupChatDomainService {

    private final CassandraGroupChatByIdRepository cassandraGroupChatByIdRepository;

    public GroupChat save(@NonNull GroupChat groupChat) {
        return cassandraGroupChatByIdRepository.save(groupChat);
    }

    public GroupChat getById(@NonNull UUID id) {
        return cassandraGroupChatByIdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat with this id does not exist"));
    }

    public Optional<GroupChat> findById(@NonNull UUID id) {
        return cassandraGroupChatByIdRepository.findById(id);
    }

}
