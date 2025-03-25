package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraMessageFileByMessageIdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraMessageFileMapper {

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "messageId", source = "key.messageId")
    MessageFile toDomain(CassandraMessageFileByMessageIdEntity entity);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.messageId", source = "messageId")
    CassandraMessageFileByMessageIdEntity toMessageFileByMessageIdEntity(MessageFile domain);

}
