package com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper;

import com.nagornov.CorporateMessenger.domain.model.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KafkaUserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "userProfilePhotos", ignore = true)
    User toDomain(KafkaUserEntity entity);

    KafkaUserEntity toKafkaUserEntity(User domain);

}
