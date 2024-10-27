package com.nagornov.CorporateMessenger.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginFormRequest {

    private @NotNull @NotBlank String username;
    private @NotNull @NotBlank String password;

}
