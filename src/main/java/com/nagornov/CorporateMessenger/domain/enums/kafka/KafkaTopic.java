package com.nagornov.CorporateMessenger.domain.enums.kafka;

import lombok.Getter;

@Getter
public enum KafkaTopic {

    UNREAD_MESSAGE_TOPIC("unread-message-topic"),
    LOG_TOPIC("log-topic");

    public static final String UNREAD_MESSAGE_TOPIC_NAME = "unread-message-topic";
    public static final String LOG_TOPIC_NAME = "log-topic";

    private final String name;

    KafkaTopic(String name) {
        this.name = name;
    }

}