package com.nagornov.CorporateMessenger.infrastructure.logback.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ThrowableMessageUtils {

    public static String formatErrorMessage(String logMessage, String throwableMessage) {
         return "%s: %s".formatted(logMessage, throwableMessage != null ? throwableMessage : "No details");
    }

}
