package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class EmployeePhoto {

    private UUID id;
    private UUID employeeId;
    private String fileName;
    private String filePath;
    private String contentType;
    private LocalDateTime createdAt;

}
