package com.nagornov.CorporateMessenger.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationFormRequest {

    private @NotNull @NotBlank String username;
    private @NotNull @NotBlank String password;

    private @NotNull @NotBlank String firstName;
    private @NotNull @NotBlank String lastName;

    private @NotNull @NotBlank String registrationKey;
}