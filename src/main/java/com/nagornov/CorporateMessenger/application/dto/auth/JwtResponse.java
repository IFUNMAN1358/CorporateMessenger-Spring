package com.nagornov.CorporateMessenger.application.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

    private @NotNull String accessToken;
    private @NotNull String refreshToken;

}
