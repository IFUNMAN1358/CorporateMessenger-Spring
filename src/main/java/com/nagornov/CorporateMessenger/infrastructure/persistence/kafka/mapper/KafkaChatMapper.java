package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper;

import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaChatEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KafkaChatMapper {

    GroupChat toGroupChatDomain(KafkaChatEntity entity);

    PrivateChat toPrivateChatDomain(KafkaChatEntity entity);

    @Mapping(target = "chatType", ignore = true)
    @Mapping(target = "firstUserId", ignore = true)
    @Mapping(target = "secondUserId", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    KafkaChatEntity toChatEntity(GroupChat domain);

    @Mapping(target = "chatType", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "isPublic", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    KafkaChatEntity toChatEntity(PrivateChat domain);


}
