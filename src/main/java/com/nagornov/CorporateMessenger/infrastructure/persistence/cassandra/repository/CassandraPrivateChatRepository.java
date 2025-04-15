package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByUsersEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraPrivateChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByFirstUserRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatBySecondUserRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraPrivateChatByUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraPrivateChatRepository {

    private final SpringDataCassandraPrivateChatByIdRepository springDataCassandraPrivateChatByIdRepository;
    private final SpringDataCassandraPrivateChatByFirstUserRepository springDataCassandraPrivateChatByFirstUserRepository;
    private final SpringDataCassandraPrivateChatBySecondUserRepository springDataCassandraPrivateChatBySecondUserRepository;
    private final SpringDataCassandraPrivateChatByUsersRepository springDataCassandraPrivateChatByUsersRepository;
    private final CassandraPrivateChatMapper cassandraPrivateChatMapper;

    public PrivateChat save(PrivateChat privateChat) {
        springDataCassandraPrivateChatByIdRepository.save(
                cassandraPrivateChatMapper.toPrivateChatByIdEntity(privateChat)
        );
        springDataCassandraPrivateChatByFirstUserRepository.save(
                cassandraPrivateChatMapper.toPrivateChatByFirstUserEntity(privateChat)
        );
        springDataCassandraPrivateChatBySecondUserRepository.save(
                cassandraPrivateChatMapper.toPrivateChatBySecondUserEntity(privateChat)
        );
        CassandraPrivateChatByUsersEntity entity = springDataCassandraPrivateChatByUsersRepository.save(
                cassandraPrivateChatMapper.toPrivateChatByUsersEntity(privateChat)
        );
        return cassandraPrivateChatMapper.toDomain(entity);
    }

    public void delete(PrivateChat privateChat) {
        springDataCassandraPrivateChatByIdRepository.delete(
                cassandraPrivateChatMapper.toPrivateChatByIdEntity(privateChat)
        );
        springDataCassandraPrivateChatByFirstUserRepository.delete(
                cassandraPrivateChatMapper.toPrivateChatByFirstUserEntity(privateChat)
        );
        springDataCassandraPrivateChatBySecondUserRepository.delete(
                cassandraPrivateChatMapper.toPrivateChatBySecondUserEntity(privateChat)
        );
        springDataCassandraPrivateChatByUsersRepository.delete(
                cassandraPrivateChatMapper.toPrivateChatByUsersEntity(privateChat)
        );
    }

    public Optional<PrivateChat> findById(UUID id) {
        return springDataCassandraPrivateChatByIdRepository
                .findCassandraPrivateChatEntityById(id)
                .map(cassandraPrivateChatMapper::toDomain);
    }

    public List<PrivateChat> getAllByFirstUserId(UUID firstUserId) {
        return springDataCassandraPrivateChatByFirstUserRepository
                .getAllByFirstUserId(firstUserId)
                .stream().map(cassandraPrivateChatMapper::toDomain).toList();
    }

    public List<PrivateChat> getAllBySecondUserId(UUID secondUserId) {
        return springDataCassandraPrivateChatBySecondUserRepository
                .getAllBySecondUserId(secondUserId)
                .stream().map(cassandraPrivateChatMapper::toDomain).toList();
    }

    public List<PrivateChat> getAllByFirstUserIdAndSecondUserId(UUID firstUserId, UUID secondUserId) {
        return springDataCassandraPrivateChatByUsersRepository
                .getAllByFirstUserIdAndSecondUserId(firstUserId, secondUserId)
                .stream().map(cassandraPrivateChatMapper::toDomain).toList();
    }
}
