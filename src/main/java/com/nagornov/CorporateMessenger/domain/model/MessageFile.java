package com.nagornov.CorporateMessenger.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageFile {

    private UUID id;
    private UUID messageId;
    private String fileName;
    private String filePath;
    private String contentType;
    private Instant createdAt;

    public void updateMessageId(@NotNull UUID newMessageId) {
        this.messageId = newMessageId;
    }

}
