package com.nagornov.CorporateMessenger.application.dto.message;

import com.nagornov.CorporateMessenger.domain.enums.WsMessageResponseType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WsMessageResponse {

    @NotNull
    private String type;

    private MessageResponse messageResponse;

    public WsMessageResponse(@NotNull WsMessageResponseType type, @NotNull MessageResponse messageResponse) {
        this.type = type.getType();
        this.messageResponse = messageResponse;
    }

}
