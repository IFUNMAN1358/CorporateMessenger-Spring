package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.message.ReadMessage;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraReadMessageByMessageIdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraReadMessageMapper {

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "messageId", source = "key.messageId")
    ReadMessage toDomain(CassandraReadMessageByMessageIdEntity entity);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.messageId", source = "messageId")
    CassandraReadMessageByMessageIdEntity toReadMessageByMessageIdEntity(ReadMessage entity);

}
