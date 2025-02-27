package com.nagornov.CorporateMessenger.sharedKernel.LogService.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogContext {

    private String traceId = null;
    private String spanId = null;
    private String userId = null;
    private String httpMethod = null;
    private String httpPath = null;

}
