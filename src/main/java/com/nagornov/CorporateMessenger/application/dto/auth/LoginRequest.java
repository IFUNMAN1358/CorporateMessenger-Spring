package com.nagornov.CorporateMessenger.application.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"password"})
public class LoginRequest {

    @NotNull(message = "Имя пользователя не может быть null")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @NotNull(message = "Пароль не может быть null")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

}
