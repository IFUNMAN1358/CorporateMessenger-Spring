package com.nagornov.CorporateMessenger.domain.model.chat;

import com.nagornov.CorporateMessenger.domain.model.interfaces.ChatPhotoInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChatPhoto implements ChatPhotoInterface {

    private UUID id;
    private Long chatId;
    private String fileName;
    private String smallFilePath;
    private String bigFilePath;
    private String mimeType;
    private Boolean isMain;
    private Instant createdAt;

}
