package com.nagornov.CorporateMessenger.domain.model.user;

import com.nagornov.CorporateMessenger.domain.model.interfaces.ChatPhotoInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPhoto implements ChatPhotoInterface {

    private UUID id;
    private UUID userId;
    private String fileName;
    private String smallFilePath;
    private String bigFilePath;
    private String mimeType;
    private Boolean isMain;
    private Instant createdAt;

    public void markAsMain() {
        this.isMain = true;
    }

    public void unmarkAsMain() {
        this.isMain = false;
    }

}