package com.nagornov.CorporateMessenger.application.dto.model.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupChatSettingsRequest {

    @NotNull(message = "Настройка 'Присоединение по запросу' не может быть null")
    private Boolean joinByRequest;

    @NotNull(message = "Настройка 'Имеет скрытых участников' не может быть null")
    private Boolean hasHiddenMembers;

}
