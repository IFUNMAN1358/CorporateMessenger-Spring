package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Employee {

    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private UUID leaderId;
    private String department;
    private String position;
    private String description;
    private String workSchedule;
    private Instant createdAt;
    private Instant updatedAt;

    public void updateFirstName(@NonNull String newFirstName) {
        this.firstName = newFirstName;
        this.updatedAt = Instant.now();
    }

    public void updateLastName(@NonNull String newLastName) {
        this.lastName = newLastName;
        this.updatedAt = Instant.now();
    }

    public void updatePatronymic(@NonNull String newPatronymic) {
        this.patronymic = newPatronymic;
        this.updatedAt = Instant.now();
    }

    public void updateLeaderId(@NonNull UUID newLeaderId) {
        this.leaderId = newLeaderId;
        this.updatedAt = Instant.now();
    }

    public void updateDepartment(@NonNull String newDepartment) {
        this.department = newDepartment;
        this.updatedAt = Instant.now();
    }

    public void updatePosition(@NonNull String newPosition) {
        this.position = newPosition;
        this.updatedAt = Instant.now();
    }

    public void updateDescription(@NonNull String newDescription) {
        this.description = newDescription;
        this.updatedAt = Instant.now();
    }

    public void updateWorkSchedule(@NonNull String newWorkSchedule) {
        this.workSchedule = newWorkSchedule;
        this.updatedAt = Instant.now();
    }

}
