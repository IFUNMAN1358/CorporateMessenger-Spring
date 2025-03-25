package com.nagornov.CorporateMessenger.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserProfilePhoto {

    private UUID id;
    private UUID userId;
    private String fileName;
    private String filePath;
    private Boolean isMain;
    private LocalDateTime createdAt;

    //
    //
    //

    public void markAsMain() {
        this.isMain = true;
    }

    public void unmarkAsMain() {
        this.isMain = false;
    }

}