package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.UserBlacklist;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserBlacklistEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaUserBlacklistMapper {

    UserBlacklist toDomain(JpaUserBlacklistEntity entity);

    JpaUserBlacklistEntity toEntity(UserBlacklist domain);

}
