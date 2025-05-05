package com.nagornov.CorporateMessenger.application.dto.model.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserIdRequest {

    @NotNull(message = "Идентификатор пользователя не может быть null")
    private UUID userId;

}
