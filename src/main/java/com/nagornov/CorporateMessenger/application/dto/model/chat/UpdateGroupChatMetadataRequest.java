package com.nagornov.CorporateMessenger.application.dto.model.chat;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateGroupChatMetadataRequest {

    @NotNull
    @NotBlank
    private String newName;

    private String newDescription;

    //
    //
    //

    @PostConstruct
    public void initialize() {
        if (this.newDescription == null || this.newDescription.isBlank()) {
            this.newDescription = null;
        }
    }

}
