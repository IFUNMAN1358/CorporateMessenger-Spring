package com.nagornov.CorporateMessenger.domain.dto;

import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.domain.model.user.EmployeePhoto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class EmployeeWithEmployeePhotoDTO {

    private Employee employee;
    private Optional<EmployeePhoto> employeePhoto;

}
