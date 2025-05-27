package com.nagornov.CorporateMessenger.application.dto.model.chat;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUsername;
import com.nagornov.CorporateMessenger.domain.annotation.enums.UsernameType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateGroupChatRequest {

    @NotNull(message = "Название чата не может быть null")
    @NotBlank(message = "Название чата не может быть пустым")
    @Size(message = "Название чата должно содержать от 1 до 128 символов", min = 1, max = 128)
    private String title;

    @ValidUsername(usernameType = UsernameType.CHAT)
    @Size(message = "Уникальное название чата должно содержать от 5 до 32 символов", min = 5, max = 32)
    private String username;

}
