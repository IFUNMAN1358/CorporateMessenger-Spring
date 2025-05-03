package com.nagornov.CorporateMessenger.domain.enums.kafka;

import lombok.Getter;

@Getter
public enum KafkaGroup {

    UNREAD_MESSAGE_GROUP("unread-message-group"),
    LOG_GROUP("log-group"),
    NOTIFICATION_GROUP("notification-group");

    public static final String UNREAD_MESSAGE_GROUP_NAME = "unread-message-group";
    public static final String LOG_GROUP_NAME = "log-group";
    public static final String NOTIFICATION_GROUP_NAME = "notification-group";

    private final String name;

    KafkaGroup(String name) {
        this.name = name;
    }

}
