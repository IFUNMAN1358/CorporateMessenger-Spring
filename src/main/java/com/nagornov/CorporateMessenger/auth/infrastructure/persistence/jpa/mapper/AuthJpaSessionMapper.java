package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthJpaSessionMapper {

    @Mapping(target = "user.id", source = "userId")
    AuthJpaSessionEntity toEntity(AuthSession domain);

    @Mapping(target = "userId", source = "user.id")
    AuthSession toDomain(AuthJpaSessionEntity entity);

}
