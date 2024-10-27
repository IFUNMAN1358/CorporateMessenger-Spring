package com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.security.domain.model.SecuritySession;
import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.model.SecurityRedisUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SecurityRedisUserMapper {

    //
    // To Domain
    //

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "session", source = "entity", qualifiedByName = "mapSessionFromEntity")
    SecurityUser toDomain(SecurityRedisUserEntity entity);

    @Named("mapSessionFromEntity")
    default SecuritySession mapSessionFromEntity(SecurityRedisUserEntity userEntity) {
        if (userEntity.getSessionId() == null) {
            return null;
        }
        return new SecuritySession(
                userEntity.getSessionId(),
                userEntity.getId(),
                null,
                null
        );
    }

    //
    // To Entity
    //

    @Mapping(target = "sessionId", source = "domain.session.id")
    SecurityRedisUserEntity toEntity(SecurityUser domain);

}
