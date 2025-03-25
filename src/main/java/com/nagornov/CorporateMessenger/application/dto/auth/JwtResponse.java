package com.nagornov.CorporateMessenger.application.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

    private String accessToken;
    private String refreshToken;

}
