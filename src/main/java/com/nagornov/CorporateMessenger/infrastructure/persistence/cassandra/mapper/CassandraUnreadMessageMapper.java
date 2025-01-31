package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.UnreadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraUnreadMessageByChatIdAndUserIdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraUnreadMessageMapper {

    @Mapping(target = "chatId", source = "key.chatId")
    @Mapping(target = "userId", source = "key.userId")
    UnreadMessage toDomain(CassandraUnreadMessageByChatIdAndUserIdEntity entity);

    @Mapping(target = "key.chatId", source = "chatId")
    @Mapping(target = "key.userId", source = "userId")
    CassandraUnreadMessageByChatIdAndUserIdEntity toUnreadMessageByChatIdAndUserIdEntity(UnreadMessage domain);

}
