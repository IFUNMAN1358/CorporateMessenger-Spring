package com.nagornov.CorporateMessenger.application.dto.auth;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUsername;
import com.nagornov.CorporateMessenger.domain.annotation.enums.UsernameType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"password", "confirmPassword"})
public class RegistrationRequest {

    @ValidUsername(usernameType = UsernameType.USER)
    @Size(message = "Имя пользователя должно содержать от 5 до 32 символов", min = 5, max = 32)
    private String username;

    @NotNull(message = "Имя не может быть null")
    @NotBlank(message = "Имя не может быть пустым")
    @Size(message = "Имя должно содержать от 1 до 64 символов", min = 1, max = 64)
    private String firstName;

    @NotNull(message = "Фамилия не может быть null")
    @Size(message = "Фамилия должна содержать от 0 до 64 символов", max = 64)
    private String lastName;

    @NotNull(message = "Пароль не может быть null")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(message = "Пароль должен содержать от 10 до 30 символов", min = 10, max = 30)
    private String password;

    @NotNull(message = "Повторный пароль не может быть null")
    @NotBlank(message = "Повторный пароль не может быть пустым")
    @Size(message = "Повторный пароль должен содержать от 10 до 30 символов", min = 10, max = 30)
    private String confirmPassword;

    @NotNull(message = "Ключ регистрации не может быть null")
    @NotBlank(message = "Ключ регистрации не может быть пустым")
    @Size(message = "Ключ регистрации должен содержать 12 символов", min = 12, max = 12)
    private String registrationKey;
}