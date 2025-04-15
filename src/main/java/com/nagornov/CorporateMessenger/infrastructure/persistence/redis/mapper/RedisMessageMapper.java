package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisMessageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RedisMessageMapper {

    Message toDomain(RedisMessageEntity redisMessageEntity);

    RedisMessageEntity toEntity(Message message);

}
