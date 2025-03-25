package com.nagornov.CorporateMessenger.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsernameRequest {

    @NotNull
    @NotBlank
    @Size
    private String newUsername;

}
