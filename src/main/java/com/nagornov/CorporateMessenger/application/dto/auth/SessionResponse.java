package com.nagornov.CorporateMessenger.application.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SessionResponse {

    private UUID sessionId;
    private String accessToken;
    private String refreshToken;

}
