package com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.security.domain.model.SecuritySession;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.redis.model.SecurityRedisSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecurityRedisSessionMapper {

    @Mapping(target = "userId", ignore = true)
    SecuritySession toDomain(SecurityRedisSessionEntity entity);

    SecurityRedisSessionEntity toEntity(SecuritySession domain);

}
