package com.nagornov.CorporateMessenger.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegistrationKey {

    private UUID id;
    private String value;
    private Boolean isApplied = false;
    private LocalDateTime createdAt;

    public void validateApplied() {
        if (this.getIsApplied()) {
            throw new RuntimeException("Registration key already applied");
        }
    }

}
