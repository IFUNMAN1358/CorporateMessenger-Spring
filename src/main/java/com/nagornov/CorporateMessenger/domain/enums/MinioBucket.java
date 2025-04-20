package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum MinioBucket {

    USER_PHOTOS("user-photos"),
    EMPLOYEE_PHOTOS("employee-photos"),
    CHAT_PHOTOS("chat-photos"),
    MESSAGE_FILES("message-files"),;

    private final String bucketName;

    MinioBucket(String bucketName) {
        this.bucketName = bucketName;
    }
}
