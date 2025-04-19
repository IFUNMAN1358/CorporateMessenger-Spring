package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByUserPairHashEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CassandraPrivateChatMapper {

    PrivateChat toDomain(CassandraPrivateChatByUserPairHashEntity entity);

    CassandraPrivateChatByUserPairHashEntity toEntity(PrivateChat domain);

}
