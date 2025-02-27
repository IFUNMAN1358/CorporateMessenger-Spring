package com.nagornov.CorporateMessenger.infrastructure.security.utils;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.net.URI;

@UtilityClass
public class UrlUtils {

    public static String normalizeUri(@NotNull String externalServiceUrl, @NotNull String externalServiceEndpoint) {
        return URI.create(externalServiceUrl).resolve(externalServiceEndpoint).toString();
    }

}