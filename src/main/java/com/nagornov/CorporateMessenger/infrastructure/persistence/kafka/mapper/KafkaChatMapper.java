package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper;

import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaChatEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KafkaChatMapper {

    Chat toDomain(KafkaChatEntity entity);

    KafkaChatEntity toEntity(Chat domain);

}
