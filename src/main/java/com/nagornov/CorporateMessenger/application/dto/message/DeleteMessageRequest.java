package com.nagornov.CorporateMessenger.application.dto.message;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DeleteMessageRequest {

    @NotBlank
    @NotNull
    @ValidUuid
    private String chatId;

    @NotBlank
    @NotNull
    @ValidUuid
    private String messageId;

    public UUID getChatIdAsUUID() {
        return UUID.fromString(this.chatId);
    }

    public UUID getMessageIdAsUUID() {
        return UUID.fromString(this.messageId);
    }

}
