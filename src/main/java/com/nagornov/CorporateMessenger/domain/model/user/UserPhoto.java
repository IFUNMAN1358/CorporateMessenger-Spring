package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPhoto {

    private UUID id;
    private UUID userId;
    private String fileName;
    private String smallFilePath;
    private Long smallFileSize;
    private String bigFilePath;
    private Long bigFileSize;
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