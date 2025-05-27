package com.nagornov.CorporateMessenger.application.dto.model.chat;

import com.nagornov.CorporateMessenger.domain.annotation.ant.ValidUsername;
import com.nagornov.CorporateMessenger.domain.annotation.enums.UsernameType;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupChatUsernameRequest {

    @ValidUsername(usernameType = UsernameType.CHAT)
    @Size(message = "Уникальное название чата должно содержать от 5 до 32 символов", min = 5, max = 32)
    private String username;

}
