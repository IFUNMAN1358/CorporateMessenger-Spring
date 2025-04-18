package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum ChatMemberStatus {

    CREATOR("creator"),
    ADMINISTRATOR("administrator"),
    MEMBER("member"),
    RESTRICTED("restricted"),
    LEFT("left"),
    KICKED("kicked");

    private final String status;

    ChatMemberStatus(String status) {
        this.status = status;
    }

}
