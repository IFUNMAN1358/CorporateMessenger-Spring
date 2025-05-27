package com.nagornov.CorporateMessenger.infrastructure.persistence.redis.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.UserBlacklist;
import com.nagornov.CorporateMessenger.infrastructure.persistence.redis.entity.RedisUserBlacklistEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RedisUserBlacklistMapper {

    UserBlacklist toDomain(RedisUserBlacklistEntity entity);

    RedisUserBlacklistEntity toEntity(UserBlacklist domain);

}
