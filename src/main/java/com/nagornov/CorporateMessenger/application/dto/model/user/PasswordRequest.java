package com.nagornov.CorporateMessenger.application.dto.model.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"currentPassword", "newPassword", "confirmNewPassword"})
public class PasswordRequest {

    @NotNull(message = "Текущий пароль не может быть null")
    @NotBlank(message = "Текущий пароль не может быть пустым")
    private String currentPassword;

    @NotNull(message = "Новый пароль не может быть null")
    @NotBlank(message = "Новый пароль не может быть пустым")
    @Size(message = "Новый пароль должен содержать от 10 до 30 символов", min = 10, max = 30)
    private String newPassword;

    @NotNull(message = "Повторный пароль не может быть null")
    @NotBlank(message = "Повторный пароль не может быть пустым")
    @Size(message = "Повторный пароль должен содержать от 10 до 30 символов", min = 10, max = 30)
    private String confirmNewPassword;

}
