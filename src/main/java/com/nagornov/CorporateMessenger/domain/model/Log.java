package com.nagornov.CorporateMessenger.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Log {

    private Instant timestamp;
    private String message;
    private String level;
    private String serviceName;
    private String fileName;
    private String methodName;
    private int lineNumber;
    private String traceId;
    private String spanId;
    private String userId;
    private String httpMethod;
    private String httpPath;

}