package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum KafkaTopic {

    TEST_TOPIC("test-topic"),
    UNREAD_MESSAGE_TOPIC("unread-message-topic");

    public static final String TEST_TOPIC_NAME = "test-topic";
    public static final String UNREAD_MESSAGE_TOPIC_NAME = "unread-message-topic";

    private final String name;

    KafkaTopic(String name) {
        this.name = name;
    }

}