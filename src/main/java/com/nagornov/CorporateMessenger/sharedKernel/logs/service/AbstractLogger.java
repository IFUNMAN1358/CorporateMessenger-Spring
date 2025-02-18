package com.nagornov.CorporateMessenger.sharedKernel.logs.service;

import com.nagornov.CorporateMessenger.sharedKernel.logs.enums.LogLevel;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.Log;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.LoggerMetadata;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

public abstract class AbstractLogger {

    @Value("${spring.application.name}")
    private String serviceName;

    abstract protected LoggerMetadata initLoggerMetadata();
    abstract protected void sendLog(Log log);

    private void initSend(String message, LogLevel logLevel, String userId) {

        LoggerMetadata loggerMetadata = initLoggerMetadata();

        Log log = new Log();
        log.setTimestamp(Instant.now());

        log.setMessage(message);
        log.setLevel(logLevel.getLevel());

        log.setServiceName(serviceName);
        log.setDirectoryName(loggerMetadata.getDirectoryName());

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains(log.getDirectoryName())) {
                log.setFileName(element.getFileName());
                log.setMethodName(element.getMethodName());
                log.setLineNumber(element.getLineNumber());
                break;
            }
        }

        log.setTraceId(LogContextHolder.getTraceId());
        log.setSpanId(LogContextHolder.getSpanId());
        log.setHttpMethod(LogContextHolder.getHttpMethod());
        log.setHttpPath(LogContextHolder.getHttpPath());

        log.setUserId(userId);

        sendLog(log);
    }

    // ALL

    public void all(String message) {
        initSend(message, LogLevel.ALL, null);
    }

    public void all(String message, String userId) {
        initSend(message, LogLevel.ALL, userId);
    }

    // TRACE

    public void trace(String message) {
        initSend(message, LogLevel.TRACE, null);
    }

    public void trace(String message, String userId) {
        initSend(message, LogLevel.TRACE, userId);
    }

    // DEBUG

    public void debug(String message) {
        initSend(message, LogLevel.DEBUG, null);
    }

    public void debug(String message, String userId) {
        initSend(message, LogLevel.DEBUG, userId);
    }

    // INFO

    public void info(String message) {
        initSend(message, LogLevel.INFO, null);
    }

    public void info(String message, String userId) {
        initSend(message, LogLevel.INFO, userId);
    }

    // WARN

    public void warn(String message) {
        initSend(message, LogLevel.WARN, null);
    }

    public void warn(String message, String userId) {
        initSend(message, LogLevel.WARN, userId);
    }

    // ERROR

    public void error(String message) {
        initSend(message, LogLevel.ERROR, null);
    }

    public void error(String message, String userId) {
        initSend(message, LogLevel.ERROR, userId);
    }

    // FATAL

    public void fatal(String message) {
        initSend(message, LogLevel.FATAL, null);
    }

    public void fatal(String message, String userId) {
        initSend(message, LogLevel.FATAL, userId);
    }

}