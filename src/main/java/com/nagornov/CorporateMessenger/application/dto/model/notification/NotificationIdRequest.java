package com.nagornov.CorporateMessenger.application.dto.model.notification;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class NotificationIdRequest {

    @NotNull(message = "Идентификатор уведомления не может быть null")
    @NotEmpty(message = "Идентификатор уведомления не может быть пустым")
    private final UUID notificationId;

}
