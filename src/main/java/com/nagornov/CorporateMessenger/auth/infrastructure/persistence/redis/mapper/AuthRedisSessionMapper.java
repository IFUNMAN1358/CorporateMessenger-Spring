package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.model.AuthRedisSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthRedisSessionMapper {

    @Mapping(target = "userId", ignore = true)
    AuthSession toDomain(AuthRedisSessionEntity entity);

    AuthRedisSessionEntity toEntity(AuthSession domain);

}
