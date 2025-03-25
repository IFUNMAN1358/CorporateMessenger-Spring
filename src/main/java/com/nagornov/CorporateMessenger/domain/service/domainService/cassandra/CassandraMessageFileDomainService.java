package com.nagornov.CorporateMessenger.domain.service.domainService.cassandra;

import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageFileByMessageIdRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CassandraMessageFileDomainService {

    private final CassandraMessageFileByMessageIdRepository cassandraMessageFileByMessageIdRepository;

    public MessageFile save(@NonNull MessageFile messageFile) {
        return cassandraMessageFileByMessageIdRepository.save(messageFile);
    }

    public void delete(@NonNull MessageFile messageFile) {
        cassandraMessageFileByMessageIdRepository.delete(messageFile);
    }

    public List<MessageFile> getAllByMessageId(@NonNull UUID messageId) {
        return cassandraMessageFileByMessageIdRepository.getAllByMessageId(messageId);
    }

    public Optional<MessageFile> findByIdAndMessageId(@NonNull UUID id, @NonNull UUID messageId) {
        return cassandraMessageFileByMessageIdRepository.findByIdAndMessageId(id, messageId);
    }

}
