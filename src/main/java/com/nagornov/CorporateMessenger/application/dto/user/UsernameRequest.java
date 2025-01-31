package com.nagornov.CorporateMessenger.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsernameRequest {

    @NotNull
    @NotBlank
    private String newUsername;

}
