package com.nagornov.CorporateMessenger.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthRegistrationKey {

    private UUID id;
    private String value;
    private Boolean isApplied;
    private LocalDateTime createdAt;

}
