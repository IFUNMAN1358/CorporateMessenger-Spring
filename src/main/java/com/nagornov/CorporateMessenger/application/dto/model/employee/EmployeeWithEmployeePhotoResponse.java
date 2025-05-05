package com.nagornov.CorporateMessenger.application.dto.model.employee;

import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.domain.model.user.EmployeePhoto;
import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

@Data
public class EmployeeWithEmployeePhotoResponse {

    private EmployeeResponse employee;
    private EmployeePhotoResponse employeePhoto;

    public EmployeeWithEmployeePhotoResponse(@NonNull Employee employee, @NonNull Optional<EmployeePhoto> employeePhoto) {
        this.employee = new EmployeeResponse(employee);
        employeePhoto.ifPresent(ep -> this.employeePhoto = new EmployeePhotoResponse(ep));
    }

}