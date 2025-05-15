package com.nagornov.CorporateMessenger.application.dto.auth;

import com.nagornov.CorporateMessenger.domain.model.auth.ExternalService;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(exclude = {"apiKey"})
public class ExternalServiceResponse {

    private UUID id;
    private String name;
    private String apiKey;
    private Boolean requiresApiKey;

    public ExternalServiceResponse(ExternalService externalService) {
        this.id = externalService.getId();
        this.name = externalService.getName();
        this.apiKey = externalService.getApiKey();
        this.requiresApiKey = externalService.getRequiresApiKey();
    }

}