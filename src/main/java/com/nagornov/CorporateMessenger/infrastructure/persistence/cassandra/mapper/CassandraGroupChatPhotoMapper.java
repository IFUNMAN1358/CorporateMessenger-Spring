package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatPhotoByChatIdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraGroupChatPhotoMapper {

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "chatId", source = "key.chatId")
    GroupChatPhoto toDomain(CassandraGroupChatPhotoByChatIdEntity entity);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.chatId", source = "chatId")
    CassandraGroupChatPhotoByChatIdEntity toEntity(GroupChatPhoto domain);

}
