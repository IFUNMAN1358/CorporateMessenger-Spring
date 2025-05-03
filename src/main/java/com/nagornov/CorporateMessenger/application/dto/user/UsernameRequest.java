package com.nagornov.CorporateMessenger.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsernameRequest {

    @NotNull(message = "Новое имя пользователя не может быть null")
    @NotBlank(message = "Новое имя пользователя не может быть пустым")
    @Size(message = "Новое имя пользователя должно содержать от 5 до 32 символов", min = 5, max = 32)
    private String newUsername;

}
