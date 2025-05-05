package com.nagornov.CorporateMessenger.application.dto.model.employee;

import com.nagornov.CorporateMessenger.domain.model.user.EmployeePhoto;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Data
public class EmployeePhotoResponse {

    private UUID id;
    private UUID employeeId;
    private String fileName;
    private String smallFilePath;
    private String bigFilePath;
    private String mimeType;
    private Instant createdAt;

    public EmployeePhotoResponse(@NonNull EmployeePhoto employeePhoto) {
        this.id = employeePhoto.getId();
        this.employeeId = employeePhoto.getEmployeeId();
        this.fileName = employeePhoto.getFileName();
        this.smallFilePath = employeePhoto.getSmallFilePath();
        this.bigFilePath = employeePhoto.getBigFilePath();
        this.mimeType = employeePhoto.getMimeType();
        this.createdAt = employeePhoto.getCreatedAt();
    }

}
