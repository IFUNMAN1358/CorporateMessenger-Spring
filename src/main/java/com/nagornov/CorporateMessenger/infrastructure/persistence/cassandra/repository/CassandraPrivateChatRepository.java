package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByUserPairHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatRepository {

    private final SpringDataCassandraPrivateChatByUserPairHashRepository
            springDataCassandraPrivateChatByUserPairHashRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;

    public PrivateChat save(PrivateChat privateChat) {
        return cassandraPrivateChatMapper.toDomain(
                springDataCassandraPrivateChatByUserPairHashRepository.save(
                        cassandraPrivateChatMapper.toEntity(privateChat)
                )
        );
    }

    public void delete(PrivateChat privateChat) {
        springDataCassandraPrivateChatByUserPairHashRepository.delete(
                cassandraPrivateChatMapper.toEntity(privateChat)
        );
    }

    public void deleteByUserPairHash(String userPairHash) {
        springDataCassandraPrivateChatByUserPairHashRepository.deleteByUserPairHash(userPairHash);
    }

    public Optional<PrivateChat> findByUserPairHash(String userPairHash) {
        return springDataCassandraPrivateChatByUserPairHashRepository.findByUserPairHash(userPairHash)
                .map(cassandraPrivateChatMapper::toDomain);
    }

}
