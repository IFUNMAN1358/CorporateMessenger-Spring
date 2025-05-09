package com.nagornov.CorporateMessenger.application.dto.model.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageContentRequest {

    @NotNull(message = "Текст сообщения не может быть null")
    @NotBlank(message = "Текст сообщения не может быть пустым")
    private String newContent;

}
