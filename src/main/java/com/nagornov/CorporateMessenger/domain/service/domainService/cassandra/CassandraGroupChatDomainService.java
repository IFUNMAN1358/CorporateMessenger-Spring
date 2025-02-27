package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.logger.DomainServiceLogger;
import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraGroupChatByIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraGroupChatDomainService {

    private final CassandraGroupChatByIdRepository cassandraGroupChatByIdRepository;
    private final DomainServiceLogger domainServiceLogger;

    public void save(@NotNull GroupChat groupChat) {
        try {
            domainServiceLogger.info("Save started for GroupChat with ID: " + groupChat.getId());

            cassandraGroupChatByIdRepository.findById(groupChat.getId())
                    .ifPresent(e -> {
                        throw new ResourceConflictException("Group chat already exists");
                    });
            cassandraGroupChatByIdRepository.saveWithoutCheck(groupChat);
        } catch (Exception e) {
            domainServiceLogger.error("Save failed", e);
            throw e;
        }
    }

    public void update(@NotNull GroupChat groupChat) {
        try {
            domainServiceLogger.info("Update started for groupChat with ID: " + groupChat.getId());

            cassandraGroupChatByIdRepository.findById(groupChat.getId())
                    .orElseThrow(() -> new ResourceConflictException("Group chat not found"));
            cassandraGroupChatByIdRepository.updateWithoutCheck(groupChat);

        } catch (Exception e) {
            domainServiceLogger.error("Update failed", e);
            throw e;
        }
    }

    public GroupChat getById(@NotNull UUID id) {
        try {
            domainServiceLogger.info("Get by id started");

            return cassandraGroupChatByIdRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Group chat with this id does not exist"));

        } catch (Exception e) {
            domainServiceLogger.error("Get by id failed", e);
            throw e;
        }
    }

    public Optional<GroupChat> findById(@NotNull UUID id) {
        try {
            domainServiceLogger.info("Get by id started");

            return cassandraGroupChatByIdRepository.findById(id);
        } catch (Exception e) {
            domainServiceLogger.error("Get by id failed", e);
            throw e;
        }
    }

}
