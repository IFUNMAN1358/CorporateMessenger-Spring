package com.nagornov.CorporateMessenger.application.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExternalServiceRequest {

    @NotNull(message = "Имя внешнего сервиса не может быть null")
    @NotBlank(message = "Имя внешнего сервиса не может быть пустым")
    @Size(message = "Имя внешнего сервиса должно содержать от 5 до 32 символов", min = 5, max = 32)
    private String name;

    @NotNull(message = "Выбор требования ключа сервиса не может быть null")
    private Boolean requiresApiKey;

}
