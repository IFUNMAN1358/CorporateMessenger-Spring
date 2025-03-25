package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserRole {

    private UUID id;
    private UUID userId;
    private UUID roleId;

}
