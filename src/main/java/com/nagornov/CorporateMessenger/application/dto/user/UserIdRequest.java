package com.nagornov.CorporateMessenger.application.dto.user;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UserIdRequest {

    @NotNull(message = "Идентификатор пользователя не может быть null")
    @NotBlank(message = "Идентификатор пользователя не может быть пустым")
    @Size(message = "Идентификатор пользователя должен содержать от 16 до 36 символов", min = 16, max = 36)
    @ValidUuid(message = "Идентификатор пользователя должен соответствовать типу UUID")
    private String userId;

    public UUID getUserIdAsUUID() {
        return UUID.fromString(userId);
    }

}
