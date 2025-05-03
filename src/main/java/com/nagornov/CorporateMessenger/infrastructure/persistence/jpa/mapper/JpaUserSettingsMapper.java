package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.UserSettings;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserSettingsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaUserSettingsMapper {

    UserSettings toDomain(JpaUserSettingsEntity entity);

    JpaUserSettingsEntity toEntity(UserSettings domain);

}
