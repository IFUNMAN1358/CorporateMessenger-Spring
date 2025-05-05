package com.nagornov.CorporateMessenger.application.dto.model.message;

import com.nagornov.CorporateMessenger.domain.enums.WsMessageResponseType;
import lombok.Data;

@Data
public class WsMessageResponse {

    private String type;

    private MessageResponse messageResponse;

    //
    //
    //

    public WsMessageResponse(WsMessageResponseType type, MessageResponse messageResponse) {
        this.type = type.getType();
        this.messageResponse = messageResponse;
    }

}
