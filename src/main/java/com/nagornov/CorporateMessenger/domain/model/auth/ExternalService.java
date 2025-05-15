package com.nagornov.CorporateMessenger.domain.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExternalService {

    private UUID id;
    private String name; // 1-32
    private String apiKey; // 0-64
    private Boolean requiresApiKey;

    public boolean isRequiresApiKey() {
        return this.requiresApiKey;
    }

    public void updateApiKey(String newApiKey) {
        if (newApiKey == null) {
            throw new RuntimeException("argument newApiKey is null");
        }
        if (newApiKey.length() != 64) {
            throw new RuntimeException("argument newApiKey should be 64 characters long");
        }
        this.apiKey = newApiKey;
    }

}
