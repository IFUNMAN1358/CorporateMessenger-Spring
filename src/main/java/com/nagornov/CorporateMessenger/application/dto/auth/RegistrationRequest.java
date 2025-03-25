package com.nagornov.CorporateMessenger.application.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"password"})
public class RegistrationRequest {

    @NotNull(message = "Имя пользователя не может быть null")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(message = "Имя пользователя должно содержать от 5 до 20 символов", min = 5, max = 20)
    private String username;

    @NotNull(message = "Пароль не может быть null")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(message = "Пароль должен содержать от 10 до 30 символов", min = 10, max = 30)
    private String password;

    @NotNull(message = "Имя не может быть null")
    @NotBlank(message = "Имя не может быть пустым")
    @Size(message = "Имя должно содержать от 2 до 20 символов", min = 2, max = 20)
    private String firstName;

    @NotNull(message = "Фамилия не может быть null")
    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(message = "Фамилия должна содержать от 2 до 20 символов", min = 2, max = 20)
    private String lastName;

    @NotNull(message = "Ключ регистрации не может быть null")
    @NotBlank(message = "Ключ регистрации не может быть пустым")
    @Size(message = "Ключ регистрации должен содержать 12 символов", min = 12, max = 12)
    private String registrationKey;
}