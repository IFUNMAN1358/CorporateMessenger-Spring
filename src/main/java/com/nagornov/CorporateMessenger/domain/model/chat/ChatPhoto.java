package com.nagornov.CorporateMessenger.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChatPhoto {

    private UUID id;
    private Long chatId;
    private String fileName;
    private String smallFilePath;
    private Long smallFileSize;
    private String bigFilePath;
    private Long bigFileSize;
    private String mimeType;
    private Boolean isMain;
    private Instant createdAt;

}
