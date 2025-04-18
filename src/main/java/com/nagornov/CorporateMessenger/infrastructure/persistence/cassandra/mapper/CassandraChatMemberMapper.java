package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatMemberByChatIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatMemberByUserIdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CassandraChatMemberMapper {

    @Mapping(target = "chatId", source = "key.chatId")
    @Mapping(target = "userId", source = "key.userId")
    ChatMember toDomain(CassandraChatMemberByChatIdEntity entity);

    @Mapping(target = "chatId", source = "key.chatId")
    @Mapping(target = "userId", source = "key.userId")
    ChatMember toDomain(CassandraChatMemberByUserIdEntity entity);

    //
    //
    //

    @Mapping(target = "key.chatId", source = "chatId")
    @Mapping(target = "key.userId", source = "userId")
    CassandraChatMemberByChatIdEntity toChatMemberByChatIdEntity(ChatMember domain);

    @Mapping(target = "key.chatId", source = "chatId")
    @Mapping(target = "key.userId", source = "userId")
    CassandraChatMemberByUserIdEntity toChatMemberByUserIdEntity(ChatMember domain);

}
