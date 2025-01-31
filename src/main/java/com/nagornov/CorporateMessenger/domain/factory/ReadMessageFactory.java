package com.nagornov.CorporateMessenger.domain.factory;

import com.nagornov.CorporateMessenger.domain.model.ReadMessage;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ReadMessageFactory {

    public static ReadMessage createWithRandomId() {
        return new ReadMessage(
                UUID.randomUUID(),
                null,
                null,
                null
        );
    }

}
