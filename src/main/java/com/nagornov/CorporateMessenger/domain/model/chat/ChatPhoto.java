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
    private String bigFilePath;
    private Instant createdAt;

}
