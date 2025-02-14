package com.nagornov.CorporateMessenger.sharedKernel.logs.utils;

import com.nagornov.CorporateMessenger.sharedKernel.logs.enums.LogLevel;
import com.nagornov.CorporateMessenger.sharedKernel.logs.model.Log;
import com.nagornov.CorporateMessenger.sharedKernel.logs.service.LogContextHolder;

import java.time.Instant;


public class LogFactory {

    public static Log initLogObject(
            String message,
            LogLevel logLevel,
            String serviceName,
            String directoryName,
            StackTraceElement relevantElement
    ) {
        return new Log(
                Instant.now(),
                logLevel.getLevel(),
                message,
                LogContextHolder.getTraceId(),
                LogContextHolder.getSpanId(),
                serviceName,
                directoryName,
                relevantElement.getFileName(),
                relevantElement.getMethodName(),
                relevantElement.getLineNumber(),
                LogContextHolder.getUserId(),
                LogContextHolder.getHttpMethod(),
                LogContextHolder.getHttpPath()
        );
    }
}
