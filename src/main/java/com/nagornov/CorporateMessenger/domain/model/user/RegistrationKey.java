package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegistrationKey {

    private UUID id;
    private UUID userId;
    private String value;
    private Boolean isApplied;
    private LocalDateTime createdAt;

    //
    //
    //

    public void initUserId(@NonNull UUID userId) {
        if (this.userId != null) {
            throw new RuntimeException("Field RegistrationKey[userId] already initialized: %s".formatted(this.userId));
        }
        this.userId = userId;
    }

    public void markAsApplied() {
        this.isApplied = true;
    }

    public void ensureNotApplied() {
        if (this.isApplied) {
            throw new RuntimeException("Key is already applied");
        }
    }

}
