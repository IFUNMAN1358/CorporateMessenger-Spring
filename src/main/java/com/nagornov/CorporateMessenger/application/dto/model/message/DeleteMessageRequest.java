package com.nagornov.CorporateMessenger.application.dto.model.message;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUuid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class DeleteMessageRequest {

    @NotNull
    @NotBlank
    @Size
    @ValidUuid
    private String chatId;

    @NotNull
    @NotBlank
    @Size
    @ValidUuid
    private String messageId;

    //
    //
    //

    public UUID getChatIdAsUUID() {
        return UUID.fromString(this.chatId);
    }

    public UUID getMessageIdAsUUID() {
        return UUID.fromString(this.messageId);
    }

}
