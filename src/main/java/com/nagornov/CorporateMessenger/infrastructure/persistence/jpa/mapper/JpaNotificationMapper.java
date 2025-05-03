package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.Notification;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaNotificationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaNotificationMapper {

    Notification toDomain(JpaNotificationEntity entity);

    JpaNotificationEntity toEntity(Notification domain);

}
