package com.nagornov.CorporateMessenger.domain.factory;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.nagornov.CorporateMessenger.domain.model.Message;
import lombok.experimental.UtilityClass;

import java.time.Instant;

@UtilityClass
public class MessageFactory {

    public static Message createWithRandomId() {
        return new Message(
                Uuids.timeBased(),
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                false,
                Instant.now()
        );
    }

}
