package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.chat.GroupChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByChatIdAndUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraGroupChatMemberByUserIdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraGroupChatMemberMapper {

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "chatId", source = "key.chatId")
    GroupChatMember toDomain(CassandraGroupChatMemberByChatIdEntity entity);

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "userId", source = "key.userId")
    GroupChatMember toDomain(CassandraGroupChatMemberByUserIdEntity entity);

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "chatId", source = "key.chatId")
    @Mapping(target = "userId", source = "key.userId")
    GroupChatMember toDomain(CassandraGroupChatMemberByChatIdAndUserIdEntity entity);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.chatId", source = "chatId")
    CassandraGroupChatMemberByChatIdEntity toGroupChatMemberByChatIdEntity(GroupChatMember domain);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.userId", source = "userId")
    CassandraGroupChatMemberByUserIdEntity toGroupChatMemberByUserIdEntity(GroupChatMember domain);

    @Mapping(target = "key.id", source = "id")
    @Mapping(target = "key.chatId", source = "chatId")
    @Mapping(target = "key.userId", source = "userId")
    CassandraGroupChatMemberByChatIdAndUserIdEntity toGroupChatMemberByChatIdAndUserIdEntity(GroupChatMember domain);

}
