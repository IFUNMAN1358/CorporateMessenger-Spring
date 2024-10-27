package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.mapper;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.entity.AuthCassandraUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthCassandraUserMapper {

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "session", ignore = true)
    AuthUser toDomain(AuthCassandraUserEntity entity);

    AuthCassandraUserEntity toEntity(AuthUser user);

}
