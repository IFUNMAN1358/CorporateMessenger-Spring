package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraPrivateChatByFirstUserRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraPrivateChatByIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraPrivateChatBySecondUserRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraPrivateChatByUsersRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraPrivateChatDomainService {

    private final CassandraPrivateChatByIdRepository cassandraPrivateChatByIdRepository;
    private final CassandraPrivateChatByFirstUserRepository cassandraPrivateChatByFirstUserRepository;
    private final CassandraPrivateChatBySecondUserRepository cassandraPrivateChatBySecondUserRepository;
    private final CassandraPrivateChatByUsersRepository cassandraPrivateChatByUsersRepository;

    public void save(@NotNull PrivateChat privateChat) {
        cassandraPrivateChatByIdRepository.findById(privateChat.getId())
                        .ifPresent(_ -> {
                            throw new ResourceConflictException("Private chat already exists during save");
                        });
        cassandraPrivateChatByIdRepository.saveWithoutCheck(privateChat);
        cassandraPrivateChatByFirstUserRepository.saveWithoutCheck(privateChat);
        cassandraPrivateChatBySecondUserRepository.saveWithoutCheck(privateChat);
        cassandraPrivateChatByUsersRepository.saveWithoutCheck(privateChat);
    }

    public void update(@NotNull PrivateChat privateChat) {
        cassandraPrivateChatByIdRepository.findById(privateChat.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Private chat not found during update"));
        cassandraPrivateChatByIdRepository.updateWithoutCheck(privateChat);
        cassandraPrivateChatByFirstUserRepository.updateWithoutCheck(privateChat);
        cassandraPrivateChatBySecondUserRepository.updateWithoutCheck(privateChat);
        cassandraPrivateChatByUsersRepository.updateWithoutCheck(privateChat);
    }

    public Optional<PrivateChat> findAvailableByFirstUserIdAndSecondUserId(@NotNull UUID firstUserId, @NotNull UUID secondUserId) {
        return cassandraPrivateChatByUsersRepository
                .getAllByFirstUserIdAndSecondUserId(firstUserId, secondUserId)
                .stream()
                .filter(PrivateChat::getIsAvailable)
                .findFirst();
    }

    public PrivateChat getAvailableById(@NotNull UUID id) {
        return cassandraPrivateChatByIdRepository.findById(id)
                .filter(PrivateChat::getIsAvailable)
                .orElseThrow(() -> new ResourceNotFoundException("Available private chat with this id not found"));
    }

    public Optional<PrivateChat> findAvailableById(@NotNull UUID id) {
            return cassandraPrivateChatByIdRepository.findById(id)
                    .filter(PrivateChat::getIsAvailable);
    }

    public List<PrivateChat> getAllAvailableByFirstUserId(@NotNull UUID firstUserId) {
        return cassandraPrivateChatByFirstUserRepository
                .getAllByFirstUserId(firstUserId)
                .stream()
                .filter(PrivateChat::getIsAvailable)
                .toList();
    }

    public List<PrivateChat> getAllAvailableBySecondUserId(@NotNull UUID secondUserId) {
        return cassandraPrivateChatBySecondUserRepository
                .getAllBySecondUserId(secondUserId)
                .stream()
                .filter(PrivateChat::getIsAvailable)
                .toList();
    }

    public List<PrivateChat> getAllAvailableByAllUserId(@NotNull UUID userId) {
        List<PrivateChat> listOfPrivateChats = new ArrayList<>();
        listOfPrivateChats.addAll(
                cassandraPrivateChatByFirstUserRepository
                        .getAllByFirstUserId(userId)
                        .stream()
                        .filter(PrivateChat::getIsAvailable)
                        .toList()
        );
        listOfPrivateChats.addAll(
                cassandraPrivateChatBySecondUserRepository
                        .getAllBySecondUserId(userId)
                        .stream()
                        .filter(PrivateChat::getIsAvailable)
                        .toList()
        );
        return listOfPrivateChats;
    }

    public Optional<PrivateChat> findById(@NotNull UUID id) {
        return cassandraPrivateChatByIdRepository.findById(id);
    }
}