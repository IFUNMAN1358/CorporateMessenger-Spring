package com.nagornov.CorporateMessenger.application.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatIdRequest {

    @NotNull(message = "Идентификатор чата не может быть null")
    @NotBlank(message = "Идентификатор чата не может быть пустым")
    private Long chatId;

}
