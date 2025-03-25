package com.nagornov.CorporateMessenger.application.dto.chat;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateGroupChatRequest {

    private MultipartFile file;

    @NotNull
    @NotBlank
    @Size
    private String name;

    private String description;

    @NotNull
    private Boolean isPublic;

    //
    //
    //

    @PostConstruct
    public void initialize() {
        if (this.description == null || this.description.isBlank()) {
            this.description = null;
        }
        if (this.file == null || this.file.isEmpty()) {
            this.file = null;
        }
    }

}
