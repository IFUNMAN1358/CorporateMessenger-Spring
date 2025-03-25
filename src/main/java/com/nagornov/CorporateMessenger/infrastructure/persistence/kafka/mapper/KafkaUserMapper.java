package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KafkaUserMapper {

    User toDomain(KafkaUserEntity entity);

    KafkaUserEntity toEntity(User domain);

}
