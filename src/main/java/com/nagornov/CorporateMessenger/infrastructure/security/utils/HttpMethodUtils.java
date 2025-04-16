package com.nagornov.CorporateMessenger.infrastructure.security.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpMethodUtils {

    public static boolean isSafeMethod(String method) {
        return "GET".equalsIgnoreCase(method) ||
               "HEAD".equalsIgnoreCase(method) ||
               "OPTIONS".equalsIgnoreCase(method) ||
               "TRACE".equalsIgnoreCase(method);
    }

}
