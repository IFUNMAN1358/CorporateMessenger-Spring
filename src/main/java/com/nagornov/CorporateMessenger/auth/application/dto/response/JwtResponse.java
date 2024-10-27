package com.nagornov.CorporateMessenger.auth.application.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

    private @NotNull String accessToken;
    private @NotNull String refreshToken;

}
