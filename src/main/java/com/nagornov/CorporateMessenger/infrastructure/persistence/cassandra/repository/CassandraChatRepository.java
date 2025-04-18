package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraChatByIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraChatByUsernameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CassandraChatRepository {

    private final SpringDataCassandraChatByIdRepository springDataCassandraChatByIdRepository;
    private final SpringDataCassandraChatByUsernameRepository springDataCassandraChatByUsernameRepository;
    private final CassandraChatMapper cassandraChatMapper;
    private final CassandraOperations cassandraOperations;

    // for test
    public void saveSpringData(Chat chat) {
        springDataCassandraChatByIdRepository.save(cassandraChatMapper.toChatByIdEntity(chat));
    }

    // for test
    public void saveBatch(Chat chat) {
        var batch = cassandraOperations.batchOps();
        batch.insert(cassandraChatMapper.toChatByIdEntity(chat));
        batch.execute();
    }

    //
    //
    //

    public Chat save(Chat chat) {
        if (chat.getUsername() != null) {
            springDataCassandraChatByUsernameRepository.save(cassandraChatMapper.toChatByUsernameEntity(chat));
        }
        CassandraChatByIdEntity entity =
                springDataCassandraChatByIdRepository.save(cassandraChatMapper.toChatByIdEntity(chat));
        return cassandraChatMapper.toDomain(entity);
    }

    public void delete(Chat chat) {
        if (chat.getUsername() != null) {
            springDataCassandraChatByUsernameRepository.delete(cassandraChatMapper.toChatByUsernameEntity(chat));
        }
        springDataCassandraChatByIdRepository.delete(cassandraChatMapper.toChatByIdEntity(chat));
    }

    public Optional<Chat> findById(Long id) {
        return springDataCassandraChatByIdRepository.findById(id).map(cassandraChatMapper::toDomain);
    }

    public Optional<Chat> findByUsername(String username) {
        return springDataCassandraChatByUsernameRepository.findByUsername(username).map(cassandraChatMapper::toDomain);
    }

}
