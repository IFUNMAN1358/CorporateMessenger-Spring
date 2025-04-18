package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatByIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatByUsernameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraChatMapper {

    Chat toDomain(CassandraChatByIdEntity entity);

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "username", source = "key.username")
    Chat toDomain(CassandraChatByUsernameEntity entity);

    //
    //
    //

    CassandraChatByIdEntity toChatByIdEntity(Chat domain);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.username", source = "username")
    CassandraChatByUsernameEntity toChatByUsernameEntity(Chat domain);

}
