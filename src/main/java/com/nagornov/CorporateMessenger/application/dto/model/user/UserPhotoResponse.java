package com.nagornov.CorporateMessenger.application.dto.model.user;

import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserPhotoResponse {

    private UUID id;
    private UUID userId;
    private String fileName;
    private String smallFilePath;
    private String bigFilePath;
    private String mimeType;
    private Boolean isMain;
    private Instant createdAt;

    public UserPhotoResponse(@NonNull UserPhoto userPhoto) {
        this.id = userPhoto.getId();
        this.userId = userPhoto.getUserId();
        this.fileName = userPhoto.getFileName();
        this.smallFilePath = userPhoto.getSmallFilePath();
        this.bigFilePath = userPhoto.getBigFilePath();
        this.mimeType = userPhoto.getMimeType();
        this.isMain = userPhoto.getIsMain();
        this.createdAt = userPhoto.getCreatedAt();
    }

}
