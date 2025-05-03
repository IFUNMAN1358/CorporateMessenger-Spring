package com.nagornov.CorporateMessenger.domain.utils;

import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class MinioUtils {

    public static String generateFilePath(String originalFilename) {
        if (originalFilename == null) {
            throw new ResourceBadRequestException("File name is null");
        }
        return "%s_%s".formatted(UUID.randomUUID(), originalFilename);
    }

}
