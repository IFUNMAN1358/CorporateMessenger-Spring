package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum ChatType {

    PRIVATE_CHAT("PRIVATE_CHAT"),
    GROUP_CHAT("GROUP_CHAT");

    private final String type;

    ChatType(String type) {
        this.type = type;
    }

}
