package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraPrivateChatRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<PrivateChat> findAvailableByFirstUserIdAndSecondUserId(@NonNull UUID firstUserId, @NonNull UUID secondUserId) {
        return cassandraPrivateChatRepository
                .getAllByFirstUserIdAndSecondUserId(firstUserId, secondUserId)
                .stream()
                .filter(PrivateChat::getIsAvailable)
                .findFirst();
    }

    public PrivateChat getAvailableById(@NonNull UUID id) {
        return cassandraPrivateChatRepository.findById(id)
                .filter(PrivateChat::getIsAvailable)
                .orElseThrow(() -> new ResourceNotFoundException("Available private chat with this id not found"));
    }

    public Optional<PrivateChat> findAvailableById(@NonNull UUID id) {
        return cassandraPrivateChatRepository.findById(id)
                .filter(PrivateChat::getIsAvailable);
    }

    public List<PrivateChat> getAllAvailableByFirstUserId(@NonNull UUID firstUserId) {
        return cassandraPrivateChatRepository
                .getAllByFirstUserId(firstUserId)
                .stream()
                .filter(PrivateChat::getIsAvailable)
                .toList();
    }

    public List<PrivateChat> getAllAvailableBySecondUserId(@NonNull UUID secondUserId) {
        return cassandraPrivateChatRepository
                .getAllBySecondUserId(secondUserId)
                .stream()
                .filter(PrivateChat::getIsAvailable)
                .toList();
    }

    public List<PrivateChat> getAllAvailableByAllUserId(@NonNull UUID userId) {
        List<PrivateChat> listOfPrivateChats = new ArrayList<>();
        listOfPrivateChats.addAll(
                cassandraPrivateChatRepository
                        .getAllByFirstUserId(userId)
                        .stream()
                        .filter(PrivateChat::getIsAvailable)
                        .toList()
        );
        listOfPrivateChats.addAll(
                cassandraPrivateChatRepository
                        .getAllBySecondUserId(userId)
                        .stream()
                        .filter(PrivateChat::getIsAvailable)
                        .toList()
        );
        return listOfPrivateChats;
    }

    public Optional<PrivateChat> findById(@NonNull UUID id) {
        return cassandraPrivateChatRepository.findById(id);
    }
}