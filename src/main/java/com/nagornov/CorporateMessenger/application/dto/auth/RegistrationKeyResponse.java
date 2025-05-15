package com.nagornov.CorporateMessenger.application.dto.auth;

import com.nagornov.CorporateMessenger.domain.model.user.RegistrationKey;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class RegistrationKeyResponse {

    private UUID id;
    private UUID userId;
    private String value;
    private Boolean isApplied;
    private Instant createdAt;

    public RegistrationKeyResponse(RegistrationKey registrationKey) {
        this.id = registrationKey.getId();
        this.userId = registrationKey.getUserId();
        this.value = registrationKey.getValue();
        this.isApplied = registrationKey.getIsApplied();
        this.createdAt = registrationKey.getCreatedAt();
    }
}