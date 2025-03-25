package com.nagornov.CorporateMessenger.domain.model.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadMessage {

    private UUID id;
    private UUID userId;
    private UUID chatId;
    private UUID messageId;

}
