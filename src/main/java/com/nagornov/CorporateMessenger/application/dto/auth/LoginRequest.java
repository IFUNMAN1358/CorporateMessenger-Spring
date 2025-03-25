package com.nagornov.CorporateMessenger.application.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"password"})
public class LoginRequest {

    @NotNull(message = "Имя пользователя не может быть null")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(message = "Имя пользователя должно содержать от 5 до 20 символов", min = 5, max = 20)
    private String username;

    @NotNull(message = "Пароль не может быть null")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(message = "Пароль должен содержать от 10 до 30 символов", min = 10, max = 30)
    private String password;

}
