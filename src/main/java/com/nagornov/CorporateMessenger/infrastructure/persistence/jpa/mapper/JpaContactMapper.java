package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.Contact;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaContactEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaContactMapper {

    Contact toDomain(JpaContactEntity entity);

    JpaContactEntity toEntity(Contact domain);

}
