package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByFirstUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatBySecondUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByUsersEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraPrivateChatByIdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraPrivateChatMapper {

    PrivateChat toDomain(CassandraPrivateChatByIdEntity entity);

    CassandraPrivateChatByIdEntity toPrivateChatByIdEntity(PrivateChat domain);

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "firstUserId", source = "key.firstUserId")
    @Mapping(target = "secondUserId", source = "key.secondUserId")
    PrivateChat toDomain(CassandraPrivateChatByUsersEntity entity);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.firstUserId", source = "firstUserId")
    @Mapping(target = "key.secondUserId", source = "secondUserId")
    CassandraPrivateChatByUsersEntity toPrivateChatByUsersEntity(PrivateChat domain);

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "firstUserId", source = "key.firstUserId")
    PrivateChat toDomain(CassandraPrivateChatByFirstUserEntity entity);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.firstUserId", source = "firstUserId")
    CassandraPrivateChatByFirstUserEntity toPrivateChatByFirstUserEntity(PrivateChat domain);

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "secondUserId", source = "key.secondUserId")
    PrivateChat toDomain(CassandraPrivateChatBySecondUserEntity entity);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.secondUserId", source = "secondUserId")
    CassandraPrivateChatBySecondUserEntity toPrivateChatBySecondUserEntity(PrivateChat domain);

}
