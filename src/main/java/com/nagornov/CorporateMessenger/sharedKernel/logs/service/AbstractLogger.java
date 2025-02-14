package com.nagornov.CorporateMessenger.sharedKernel.logs.service;

import com.nagornov.CorporateMessenger.sharedKernel.logs.enums.LogLevel;
import com.nagornov.CorporateMessenger.sharedKernel.logs.interfaces.LogSender;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.Log;
import com.nagornov.CorporateMessenger.sharedKernel.logs.utils.LogFactory;
import jakarta.validation.constraints.NotNull;

public abstract class AbstractLogger {

    private final String serviceName;
    private final String directoryName;
    private final LogSender logSender;

   protected AbstractLogger(
           String serviceName,
           String directoryName,
           LogSender logSender
   ) {
       this.serviceName = serviceName;
       this.directoryName = directoryName;
       this.logSender = logSender;
   }

    private void sendInit(
            @NotNull String message,
            @NotNull LogLevel logLevel
    ) {
       StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

       StackTraceElement relevantElement = null;

       for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains(directoryName)) {
                relevantElement = element;
                break;
            }
       }

       Log log = LogFactory.initLogObject(message, logLevel, serviceName, directoryName, relevantElement);
       logSender.sendLog(log);
    }

    public void log(String message, LogLevel logLevel) {
       sendInit(message, logLevel);
    }

    public void all(String message) {
        log(message, LogLevel.ALL);
    }

    public void trace(@NotNull String message) {
        log(message, LogLevel.TRACE);
    }

    public void debug(@NotNull String message) {
        log(message, LogLevel.DEBUG);
    }

    public void info(@NotNull String message) {
        log(message, LogLevel.INFO);
    }

    public void warn(@NotNull String message) {
        log(message, LogLevel.WARN);
    }

    public void error(@NotNull String message) {
        log(message, LogLevel.ERROR);
    }

    public void fatal(@NotNull String message) {
        log(message, LogLevel.FATAL);
    }

}