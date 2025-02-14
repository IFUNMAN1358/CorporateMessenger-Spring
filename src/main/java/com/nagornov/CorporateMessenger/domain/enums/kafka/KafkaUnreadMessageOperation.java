package com.nagornov.CorporateMessenger.domain.enums.kafka;

import lombok.Getter;

@Getter
public enum KafkaUnreadMessageOperation {

    INCREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER("increment_unread_message_count_for_other"),
    DECREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER("decrement_unread_message_count_for_other"),
    INCREMENT_UNREAD_MESSAGE_COUNT_FOR_USER("increment_unread_message_count_for_user"),
    DECREMENT_UNREAD_MESSAGE_COUNT_FOR_USER("decrement_unread_message_count_for_user");

    public static final String INCREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER_OPERATION = "increment_unread_message_count_for_other";
    public static final String DECREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER_OPERATION = "decrement_unread_message_count_for_other";
    public static final String INCREMENT_UNREAD_MESSAGE_COUNT_FOR_USER_OPERATION = "increment_unread_message_count_for_user";
    public static final String DECREMENT_UNREAD_MESSAGE_COUNT_FOR_USER_OPERATION = "decrement_unread_message_count_for_user";

    private final String operation;

    KafkaUnreadMessageOperation(String operation) {
        this.operation = operation;
    }

}
