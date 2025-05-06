package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeeEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaEmployeePhotoEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class EmployeeWithEmployeePhotoDTOEntity implements Serializable {

    private JpaEmployeeEntity employee;
    private Optional<JpaEmployeePhotoEntity> employeePhoto;


    public EmployeeWithEmployeePhotoDTOEntity(JpaEmployeeEntity employee, JpaEmployeePhotoEntity employeePhoto) {
        this.employee = employee;
        this.employeePhoto = Optional.ofNullable(employeePhoto);
    }
}
