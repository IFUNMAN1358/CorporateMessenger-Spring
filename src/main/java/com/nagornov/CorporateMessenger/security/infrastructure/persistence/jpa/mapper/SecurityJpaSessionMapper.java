package com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.security.domain.model.SecuritySession;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.entity.SecurityJpaSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecurityJpaSessionMapper {

    @Mapping(target = "user.id", source = "userId")
    SecurityJpaSessionEntity toEntity(SecuritySession domain);

    @Mapping(target = "userId", source = "user.id")
    SecuritySession toDomain(SecurityJpaSessionEntity entity);

}
