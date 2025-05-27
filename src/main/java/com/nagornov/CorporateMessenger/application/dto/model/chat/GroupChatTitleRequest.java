package com.nagornov.CorporateMessenger.application.dto.model.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupChatTitleRequest {

    @NotNull(message = "Название чата не может быть null")
    @NotBlank(message = "Название чата не может быть пустым")
    @Size(message = "Название чата должно содержать от 1 до 128 символов", min = 1, max = 128)
    private String title;

}
