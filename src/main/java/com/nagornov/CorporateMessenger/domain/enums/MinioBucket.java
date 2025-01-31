package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum MinioBucket {

    USER_PROFILE_PHOTOS("user-profile-photos"),
    GROUP_CHAT_PHOTOS("group-chat-photos"),
    MESSAGE_FILES("message-files"),;

    private final String bucketName;

    MinioBucket(String bucketName) {
        this.bucketName = bucketName;
    }
}
