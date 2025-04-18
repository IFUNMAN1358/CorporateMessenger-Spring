package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatPhotoByChatIdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraChatPhotoMapper {

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "chatId", source = "key.chatId")
    ChatPhoto toDomain(CassandraChatPhotoByChatIdEntity entity);

    //
    //
    //

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.chatId", source = "chatId")
    CassandraChatPhotoByChatIdEntity toChatPhotoByChatIdEntity(ChatPhoto domain);

}
