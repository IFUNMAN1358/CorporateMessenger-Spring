package com.nagornov.CorporateMessenger.application.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class ChatIdRequest {

    @NotNull(message = "Идентификатор чата не может быть null")
    @NotBlank(message = "Идентификатор чата не может быть пустым")
    private String chatId;

    public Long getChatIdAsLong() {
        return Long.valueOf(this.chatId);
    }

}
