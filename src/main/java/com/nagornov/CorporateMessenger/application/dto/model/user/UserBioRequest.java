package com.nagornov.CorporateMessenger.application.dto.model.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserBioRequest {

    @NotNull(message = "Биография не может быть null")
    @Size(message = "Максимальная длина биографии - 70 символов", max = 70)
    private String bio;

}
