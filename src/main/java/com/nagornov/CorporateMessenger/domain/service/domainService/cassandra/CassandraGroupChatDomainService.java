package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatByIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraGroupChatDomainService {

    private final CassandraGroupChatByIdRepository cassandraGroupChatByIdRepository;

    public void save(@NotNull GroupChat groupChat) {
        cassandraGroupChatByIdRepository.findById(groupChat.getId())
                .ifPresent(e -> {
                    throw new ResourceConflictException("Group chat already exists");
                });
        cassandraGroupChatByIdRepository.saveWithoutCheck(groupChat);
    }

    public void update(@NotNull GroupChat groupChat) {
        cassandraGroupChatByIdRepository.findById(groupChat.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Group chat not found during update"));
        cassandraGroupChatByIdRepository.updateWithoutCheck(groupChat);
    }

    public GroupChat getById(@NotNull UUID id) {
        return cassandraGroupChatByIdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group chat with this id does not exist"));
    }

    public Optional<GroupChat> findById(@NotNull UUID id) {
        return cassandraGroupChatByIdRepository.findById(id);
    }

}
