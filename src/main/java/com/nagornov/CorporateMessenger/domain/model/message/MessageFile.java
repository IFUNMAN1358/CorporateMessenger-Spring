package com.nagornov.CorporateMessenger.domain.model.message;

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
    private String smallFilePath;
    private String bigFilePath;
    private String mimeType;
    private Instant createdAt;

}
