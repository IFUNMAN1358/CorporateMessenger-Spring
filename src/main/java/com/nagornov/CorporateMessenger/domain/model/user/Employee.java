package com.nagornov.CorporateMessenger.domain.model.user;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter
public class Employee {

    private UUID id;
    private UUID userId;
    private UUID leaderId;
    private String department;      // 0-255
    private String position;        // 0-255
    private String description;     // 0-255
    private String workSchedule;    // 0-255
    private Instant createdAt;
    private Instant updatedAt;

    //
    //
    //

    public Employee(
            UUID id,
            UUID userId,
            UUID leaderId,
            String department,
            String position,
            String description,
            String workSchedule,
            Instant createdAt,
            Instant updatedAt
    ) {
        // Validation
        validateDepartment(department);
        validatePosition(position);
        validateDescription(description);
        validateWorkSchedule(workSchedule);
        // Setting
        this.id = id;
        this.userId = userId;
        this.leaderId = leaderId;
        this.department = department;
        this.position = position;
        this.description = description;
        this.workSchedule = workSchedule;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //
    //
    //

    public String getIdAdString() {
        return this.id.toString();
    }

    public String getUserIdAsString() {
        return this.userId.toString();
    }

    public String getLeaderIdAsString() {
        return this.leaderId.toString();
    }

    public LocalDateTime getCreatedAtAsLocalDateTimeByZoneIdSystemDefault() {
        return LocalDateTime.ofInstant(this.createdAt, ZoneId.systemDefault());
    }

    public LocalDateTime getUpdatedAtAsLocalDateTimeByZoneIdSystemDefault() {
        return LocalDateTime.ofInstant(this.updatedAt, ZoneId.systemDefault());
    }

    //
    //
    //

    public void updateLeaderId(UUID newLeaderId) {
        this.leaderId = newLeaderId;
        this.updatedAt = Instant.now();
    }

    public void updateDepartment(String newDepartment) {
        validateDepartment(newDepartment);
        this.department = newDepartment;
        this.updatedAt = Instant.now();
    }

    public void updatePosition(String newPosition) {
        validatePosition(newPosition);
        this.position = newPosition;
        this.updatedAt = Instant.now();
    }

    public void updateDescription(String newDescription) {
        validateDescription(newDescription);
        this.description = newDescription;
        this.updatedAt = Instant.now();
    }

    public void updateWorkSchedule(String newWorkSchedule) {
        validateWorkSchedule(newWorkSchedule);
        this.workSchedule = newWorkSchedule;
        this.updatedAt = Instant.now();
    }

    //
    //
    //

    public void validateDepartment(String department) {
        if (department != null && department.strip().length() > 255) {
            throw new ResourceConflictException("Employee[department] should be between 0 and 255");
        }
    }

    public void validatePosition(String position) {
        if (position != null && position.strip().length() > 255) {
            throw new ResourceConflictException("Employee[position] should be between 0 and 255");
        }
    }

    public void validateDescription(String description) {
        if (description != null && description.strip().length() > 255) {
            throw new ResourceConflictException("Employee[description] should be between 0 and 255");
        }
    }

    public void validateWorkSchedule(String workSchedule) {
        if (workSchedule != null && workSchedule.strip().length() > 255) {
            throw new ResourceConflictException("Employee[workSchedule] should be between 0 and 255");
        }
    }

}
