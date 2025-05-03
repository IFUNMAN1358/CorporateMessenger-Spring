package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.domain.model.user.EmployeePhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeePhotoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JpaEmployeePhotoMapper {

    EmployeePhoto toDomain(JpaEmployeePhotoEntity entity);

    JpaEmployeePhotoEntity toEntity(EmployeePhoto domain);

}
