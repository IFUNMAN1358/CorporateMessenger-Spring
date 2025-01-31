package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum ContentType {

    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_MARKDOWN("text/markdown"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    IMAGE_SVG("image/svg+xml"),
    IMAGE_WEBP("image/webp"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_DOC("application/msword"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_ZIP("application/zip");

    private final String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }
}
