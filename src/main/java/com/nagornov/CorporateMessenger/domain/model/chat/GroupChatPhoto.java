package com.nagornov.CorporateMessenger.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GroupChatPhoto {

    private UUID id;
    private UUID chatId;
    private String fileName;
    private String filePath;
    private String contentType;
    private Instant createdAt;

}
