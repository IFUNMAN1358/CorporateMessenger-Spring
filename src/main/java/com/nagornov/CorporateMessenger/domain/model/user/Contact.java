package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Contact {

    private UUID id;
    private UUID userId;
    private UUID contactId;
    private String status;
    private LocalDateTime addedAt;

}
