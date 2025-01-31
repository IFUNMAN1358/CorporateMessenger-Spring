package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatByIdEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CassandraGroupChatMapper {

    GroupChat toDomain(CassandraGroupChatByIdEntity entity);

    CassandraGroupChatByIdEntity toGroupChatByIdEntity(GroupChat domain);

}
