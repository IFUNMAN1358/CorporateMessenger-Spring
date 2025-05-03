package com.nagornov.CorporateMessenger.domain.service.message;

import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraMessageFileRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageFileService {

    private final CassandraMessageFileRepository cassandraMessageFileRepository;

    public MessageFile save(@NonNull MessageFile messageFile) {
        return cassandraMessageFileRepository.save(messageFile);
    }

    public void delete(@NonNull MessageFile messageFile) {
        cassandraMessageFileRepository.delete(messageFile);
    }

    public List<MessageFile> getAllByMessageId(@NonNull UUID messageId) {
        return cassandraMessageFileRepository.getAllByMessageId(messageId);
    }

    public Optional<MessageFile> findByIdAndMessageId(@NonNull UUID id, @NonNull UUID messageId) {
        return cassandraMessageFileRepository.findByIdAndMessageId(id, messageId);
    }

}
