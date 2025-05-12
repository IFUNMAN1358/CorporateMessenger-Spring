package com.nagornov.CorporateMessenger.domain.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExternalService {

    private UUID id;
    private String name; // 32
    private String apiKey; // 64
    private Boolean requiresApiKey;

    public boolean isRequiresApiKey() {
        return this.requiresApiKey;
    }

}
