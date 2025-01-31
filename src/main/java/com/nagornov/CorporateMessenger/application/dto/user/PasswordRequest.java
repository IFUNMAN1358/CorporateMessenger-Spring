package com.nagornov.CorporateMessenger.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordRequest {

    @NotNull
    @NotBlank
    private String currentPassword;

    @NotNull
    @NotBlank
    private String newPassword;

}
