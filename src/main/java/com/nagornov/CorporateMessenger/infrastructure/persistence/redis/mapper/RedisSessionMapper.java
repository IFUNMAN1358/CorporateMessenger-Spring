package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.domain.model.auth.Session;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisSessionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RedisSessionMapper {

    Session toDomain(RedisSessionEntity entity);

    RedisSessionEntity toEntity(Session domain);

}
