package com.nagornov.CorporateMessenger.security.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class SecuritySession {

    private UUID id;
    private UUID userId;
    private String accessToken;
    private String refreshToken;

}
