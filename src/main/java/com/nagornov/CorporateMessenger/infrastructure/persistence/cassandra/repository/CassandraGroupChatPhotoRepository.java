package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatPhotoByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraGroupChatPhotoMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraGroupChatPhotoByChatIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraGroupChatPhotoRepository {

    private final SpringDataCassandraGroupChatPhotoByChatIdRepository springDataCassandraGroupChatPhotoByChatIdRepository;
    private final CassandraGroupChatPhotoMapper cassandraGroupChatPhotoMapper;

    public GroupChatPhoto save(GroupChatPhoto groupChatPhoto) {
        CassandraGroupChatPhotoByChatIdEntity entity = springDataCassandraGroupChatPhotoByChatIdRepository.save(
                cassandraGroupChatPhotoMapper.toEntity(groupChatPhoto)
        );
        return cassandraGroupChatPhotoMapper.toDomain(entity);
    }

    public void delete(GroupChatPhoto groupChatPhoto) {
        springDataCassandraGroupChatPhotoByChatIdRepository.delete(
                cassandraGroupChatPhotoMapper.toEntity(groupChatPhoto)
        );
    }

    public List<GroupChatPhoto> findAllByChatId(UUID chatId) {
        return springDataCassandraGroupChatPhotoByChatIdRepository
                .findAllByChatId(chatId)
                .stream().map(cassandraGroupChatPhotoMapper::toDomain)
                .toList();
    }
}
