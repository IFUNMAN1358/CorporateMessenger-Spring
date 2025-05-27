package com.nagornov.CorporateMessenger.application.dto.model.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserFirstNameAndLastNameRequest {

    @NotNull(message = "Имя не может быть null")
    @NotBlank(message = "Имя не может быть пустым")
    @Size(message = "Имя должно содержать от 1 до 64 символов", min = 1, max = 64)
    private String firstName;

    @NotNull(message = "Фамилия не может быть null")
    @Size(message = "Фамилия должна содержать от 0 до 64 символов", max = 64)
    private String lastName;

}
