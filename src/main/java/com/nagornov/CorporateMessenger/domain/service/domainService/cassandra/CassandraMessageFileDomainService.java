package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.model.MessageFile;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageFileByMessageIdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraMessageFileDomainService {

    private final CassandraMessageFileByMessageIdRepository cassandraMessageFileByMessageIdRepository;

    public void save(@NotNull MessageFile messageFile) {
        cassandraMessageFileByMessageIdRepository.saveWithoutCheck(messageFile);
    }

    public void update(@NotNull MessageFile messageFile) {
        cassandraMessageFileByMessageIdRepository.updateWithoutCheck(messageFile);
    }

    public void delete(@NotNull MessageFile messageFile) {
        cassandraMessageFileByMessageIdRepository.deleteWithoutCheck(messageFile);
    }

    public List<MessageFile> getAllByMessageId(@NotNull UUID messageId) {
        return cassandraMessageFileByMessageIdRepository.getAllByMessageId(messageId);
    }

    public Optional<MessageFile> findByIdAndMessageId(@NotNull UUID id, @NotNull UUID messageId) {
        return cassandraMessageFileByMessageIdRepository.findByIdAndMessageId(id, messageId);
    }

}
