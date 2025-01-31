package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum KafkaGroup {

    TEST_GROUP("test-group"),
    UNREAD_MESSAGE_GROUP("unread-message-group");

    public static final String TEST_GROUP_NAME = "test-group";
    public static final String UNREAD_MESSAGE_GROUP_NAME = "unread-message-group";

    private final String name;

    KafkaGroup(String name) {
        this.name = name;
    }

}
