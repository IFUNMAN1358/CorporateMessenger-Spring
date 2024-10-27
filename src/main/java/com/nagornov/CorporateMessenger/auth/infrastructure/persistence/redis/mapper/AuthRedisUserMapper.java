package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.redis.model.AuthRedisUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AuthRedisUserMapper {

    //
    // TO DOMAIN
    //

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "session", source = "entity", qualifiedByName = "mapSessionFromEntity")
    AuthUser toDomain(AuthRedisUserEntity entity);

    @Named("mapSessionFromEntity")
    default AuthSession mapSessionFromEntity(AuthRedisUserEntity redisUserEntity) {
        if (redisUserEntity.getSessionId() == null) {
            return null;
        }
        return new AuthSession(
            redisUserEntity.getSessionId(),
            redisUserEntity.getId(),
            null,
            null,
            null,
            null
        );
    }

    //
    // TO ENTITY
    //

    @Mapping(target = "sessionId", source = "domain.session.id")
    AuthRedisUserEntity toEntity(AuthUser domain);

}
