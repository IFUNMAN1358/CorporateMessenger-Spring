package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class EmployeePhoto {

    private UUID id;
    private UUID employeeId;
    private String fileName;
    private String smallFilePath;
    private Long smallFileSize;
    private String bigFilePath;
    private Long bigFileSize;
    private String mimeType;
    private Instant createdAt;

}
