package com.nagornov.CorporateMessenger.sharedKernel.logs.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogContext {

    private String traceId = null;
    private String spanId = null;
    private String httpMethod = null;
    private String httpPath = null;

}
