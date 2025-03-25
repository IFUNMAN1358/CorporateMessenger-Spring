package com.nagornov.CorporateMessenger.application.dto.chat;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class ChatIdRequest {

    @NotNull
    @NotBlank
    @Size
    @ValidUuid
    private String chatId;

    //
    //
    //

    public UUID getChatIdAsUUID() {
        return UUID.fromString(chatId);
    }

}
