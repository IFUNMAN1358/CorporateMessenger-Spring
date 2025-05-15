package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.domain.model.auth.ExternalService;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisExternalServiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RedisExternalServiceMapper {

    ExternalService toDomain(RedisExternalServiceEntity entity);

    RedisExternalServiceEntity toEntity(ExternalService domain);

}
