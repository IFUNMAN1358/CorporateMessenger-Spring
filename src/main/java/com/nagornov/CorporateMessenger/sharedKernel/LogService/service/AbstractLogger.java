package com.nagornov.CorporateMessenger.sharedKernel.LogService.service;

import com.nagornov.CorporateMessenger.sharedKernel.LogService.enums.BaseLogLevel;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.interfaces.LogLevel;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.model.Log;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.model.LoggerMetadata;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.utils.LogLevelManager;
import com.nagornov.CorporateMessenger.sharedKernel.LogService.utils.ThrowableMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

public abstract class AbstractLogger {

    @Autowired
    private LogLevelManager logLevelManager;

    @Value("${spring.application.name}")
    private String serviceName;

    abstract public LoggerMetadata initLoggerMetadata();

    abstract public void sendLog(Log log);


    private Log initLogObject(String message, LogLevel logLevel, Throwable throwable) {
        LoggerMetadata loggerMetadata = initLoggerMetadata();

        Log log = new Log();
        log.setTimestamp(Instant.now());
        log.setMessage(
                throwable == null ? message : ThrowableMessageUtils.formatErrorMessage(message, throwable.getMessage())
        );
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
        log.setUserId(LogContextHolder.getUserId());
        log.setHttpMethod(LogContextHolder.getHttpMethod());
        log.setHttpPath(LogContextHolder.getHttpPath());

        return log;
    }

    private void initSend(String message, LogLevel logLevel, Throwable throwable) {

        if (logLevelManager.isAccessibleLevel(logLevel)) {

            final Log log = initLogObject(message, logLevel, throwable);
            sendLog(log);

        }
    }

    // LOG

    public void log(String message, LogLevel logLevel) {
        initSend(message, logLevel, null);
    }

    public void log(String message, LogLevel logLevel, Throwable throwable) {
        initSend(message, logLevel, throwable);
    }

    // TRACE

    public void trace(String message) {
        initSend(message, BaseLogLevel.TRACE, null);
    }

    public void trace(String message, Throwable throwable) {
        initSend(message, BaseLogLevel.TRACE, throwable);
    }

    // DEBUG

    public void debug(String message) {
        initSend(message, BaseLogLevel.DEBUG, null);
    }

    public void debug(String message, Throwable throwable) {
        initSend(message, BaseLogLevel.DEBUG, throwable);
    }

    // INFO

    public void info(String message) {
        initSend(message, BaseLogLevel.INFO, null);
    }

    public void info(String message, Throwable throwable) {
        initSend(message, BaseLogLevel.INFO, throwable);
    }

    // WARN

    public void warn(String message) {
        initSend(message, BaseLogLevel.WARN, null);
    }

    public void warn(String message, Throwable throwable) {
        initSend(message, BaseLogLevel.WARN, throwable);
    }

    // ERROR

    public void error(String message) {
        initSend(message, BaseLogLevel.ERROR, null);
    }

    public void error(String message, Throwable throwable) {
        initSend(message, BaseLogLevel.ERROR, throwable);
    }

    // FATAL

    public void fatal(String message) {
        initSend(message, BaseLogLevel.FATAL, null);
    }

    public void fatal(String message, Throwable throwable) {
        initSend(message, BaseLogLevel.FATAL, throwable);
    }

}