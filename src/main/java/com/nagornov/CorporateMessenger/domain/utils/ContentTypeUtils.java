package com.nagornov.CorporateMessenger.domain.utils;

import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContentTypeUtils {

    public static void validateAsImageFromContentType(String contentType) {
        if (contentType == null) {
            throw new ResourceBadRequestException("File contentType is null");
        }
        if (!contentType.startsWith("image/")) {
            throw new ResourceBadRequestException("Invalid file format. File should be a image/* contentType.");
        }
    }

}
