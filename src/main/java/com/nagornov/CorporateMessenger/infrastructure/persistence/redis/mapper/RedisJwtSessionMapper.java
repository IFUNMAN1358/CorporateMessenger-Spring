package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.domain.model.auth.JwtSession;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisJwtSessionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RedisJwtSessionMapper {

    JwtSession toDomain(RedisJwtSessionEntity entity);

    RedisJwtSessionEntity toEntity(JwtSession jwtSession);

}
