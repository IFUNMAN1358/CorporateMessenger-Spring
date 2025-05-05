package com.nagornov.CorporateMessenger.application.dto.model.employee;

import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Data
public class EmployeeResponse {

    private UUID id;
    private UUID userId;
    private UUID leaderId;
    private String department;
    private String position;
    private String description;
    private String workSchedule;
    private Instant createdAt;
    private Instant updatedAt;

    public EmployeeResponse(@NonNull Employee employee) {
        this.id = employee.getId();
        this.userId = employee.getUserId();
        this.leaderId = employee.getLeaderId();
        this.department = employee.getDepartment();
        this.position = employee.getPosition();
        this.description = employee.getDescription();
        this.workSchedule = employee.getWorkSchedule();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
    }

}
