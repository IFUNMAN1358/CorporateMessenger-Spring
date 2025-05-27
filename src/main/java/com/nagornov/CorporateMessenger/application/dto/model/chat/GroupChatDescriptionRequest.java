package com.nagornov.CorporateMessenger.application.dto.model.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupChatDescriptionRequest {

    @NotNull(message = "Описание чата не может быть null")
    @Size(message = "Описание чата не должно быть длинее 255 символов", max = 255)
    private String description;

}
