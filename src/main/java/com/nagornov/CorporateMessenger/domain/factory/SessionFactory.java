package com.nagornov.CorporateMessenger.domain.factory;

import com.nagornov.CorporateMessenger.domain.model.Session;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class SessionFactory {

    public static Session createWithRandomId() {
        return new Session(
                UUID.randomUUID(),
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}