package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPhoto {

    private UUID id;
    private UUID userId;
    private String fileName;
    private String filePath;
    private String contentType;
    private Boolean isMain;
    private LocalDateTime createdAt;

    public void markAsMain() {
        this.isMain = true;
    }

    public void unmarkAsMain() {
        this.isMain = false;
    }

}