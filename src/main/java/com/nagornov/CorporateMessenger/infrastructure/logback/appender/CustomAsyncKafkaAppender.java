package com.nagornov.CorporateMessenger.infrastructure.logback.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CustomAsyncKafkaAppender extends AsyncAppender {

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (eventObject.getCallerData() == null || eventObject.getCallerData().length == 0) {
            eventObject.prepareForDeferredProcessing();
        }
        super.append(eventObject);
    }
}
