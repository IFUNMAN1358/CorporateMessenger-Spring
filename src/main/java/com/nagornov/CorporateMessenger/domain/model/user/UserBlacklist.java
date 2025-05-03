package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserBlacklist {

    private UUID id;
    private UUID userId;
    private UUID blockedUserId;
    private Instant blockedAt;

}
