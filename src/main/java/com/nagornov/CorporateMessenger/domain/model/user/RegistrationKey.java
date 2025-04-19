package com.nagornov.CorporateMessenger.domain.model.user;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegistrationKey {

    private UUID id;
    private UUID userId;
    private String value;
    private Boolean isApplied;
    private Instant createdAt;

    public void initUserId(@NonNull UUID userId) {
        if (this.userId != null) {
            throw new ResourceConflictException(
                    "Field RegistrationKey[userId] already initialized: %s".formatted(this.userId),
                    List.of(
                            new FieldError("registrationKey", "Ключ уже имеет идентификатор пользователя")
                    )
            );
        }
        this.userId = userId;
    }

    public void markAsApplied() {
        this.isApplied = true;
    }

    public void ensureNotApplied() {
        if (this.isApplied) {
            throw new ResourceConflictException(
                    "RegistrationKey already applied",
                    List.of(
                            new FieldError("registrationKey", "Ключ регистрации уже применён")
                    )
            );
        }
    }

}
