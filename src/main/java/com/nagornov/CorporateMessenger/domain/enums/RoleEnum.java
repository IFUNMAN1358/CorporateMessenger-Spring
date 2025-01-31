package com.nagornov.CorporateMessenger.domain.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {

    USER("ROLE_USER"),
    MANAGER("ROLE_MANAGER"),
    ADMIN("ROLE_ADMIN");

    private final String name;

    RoleEnum(String name) {
        this.name = name;
    }

}
