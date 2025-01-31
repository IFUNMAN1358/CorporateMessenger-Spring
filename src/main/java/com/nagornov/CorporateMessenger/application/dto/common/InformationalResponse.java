package com.nagornov.CorporateMessenger.application.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InformationalResponse {

    private @NotNull String message;

}
