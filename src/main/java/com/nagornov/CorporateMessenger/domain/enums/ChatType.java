package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum ChatType {

    PRIVATE("private"),
    GROUP("group");

    private final String type;

    ChatType(String type) {
        this.type = type;
    }

}
