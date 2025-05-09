package com.nagornov.CorporateMessenger.application.dto.model.chat;

import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.model.interfaces.ChatPhotoResponseInterface;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Data
public class ChatPhotoResponse implements ChatPhotoResponseInterface {

    private UUID id;
    private Long chatId;
    private String fileName;
    private String smallFilePath;
    private String bigFilePath;
    private String mimeType;
    private Boolean isMain;
    private Instant createdAt;

    public ChatPhotoResponse(@NonNull ChatPhoto chatPhoto) {
        this.id = chatPhoto.getId();
        this.chatId = chatPhoto.getChatId();
        this.fileName = chatPhoto.getFileName();
        this.smallFilePath = chatPhoto.getSmallFilePath();
        this.bigFilePath = chatPhoto.getBigFilePath();
        this.mimeType = chatPhoto.getMimeType();
        this.isMain = chatPhoto.getIsMain();
        this.createdAt = chatPhoto.getCreatedAt();
    }

}
