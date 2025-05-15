package com.nagornov.CorporateMessenger.domain.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class RandomStringGenerator {

    private static final String SAFE_CHARACTERS =
        "ABCDEFGHJKLMNPQRSTUVWXYZ" +
        "abcdefghjkmnpqrstuvwxyz" +
        "23456789" +
        "_-";

    private static final SecureRandom RANDOM = new SecureRandom();


    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }

        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(SAFE_CHARACTERS.length());
            result.append(SAFE_CHARACTERS.charAt(index));
        }
        return result.toString();
    }
}
