package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageByIdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraMessageMapper {

    Message toDomain(CassandraMessageByIdEntity entity);

    CassandraMessageByIdEntity toMessageByIdEntity(Message domain);

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "chatId", source = "key.chatId")
    Message toDomain(CassandraMessageByChatIdEntity entity);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.chatId", source = "chatId")
    CassandraMessageByChatIdEntity toMessageByChatIdEntity(Message domain);

}
