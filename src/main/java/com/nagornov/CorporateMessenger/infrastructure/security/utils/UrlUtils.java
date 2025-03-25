package com.nagornov.CorporateMessenger.infrastructure.security.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.net.URI;

@UtilityClass
public class UrlUtils {

    public static String normalizeUri(@NonNull String externalServiceUrl, @NonNull String externalServiceEndpoint) {
        return URI.create(externalServiceUrl).resolve(externalServiceEndpoint).toString();
    }

}