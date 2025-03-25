package com.nagornov.CorporateMessenger.domain.aspect.utils;

import lombok.experimental.UtilityClass;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;

@UtilityClass
public class AspectCallerManager {

    public static void saveCallerDataToMDC(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String className = signature.getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        int lineNumber = -1;

        MDC.put("isAspectLogger", "true");
        MDC.put("className", className);
        MDC.put("methodName", methodName);
        MDC.put("lineNumber", String.valueOf(lineNumber));
    }

    public static void clearCallerData() {
        MDC.remove("isAspectLogger");
        MDC.remove("className");
        MDC.remove("methodName");
        MDC.remove("lineNumber");
    }

}
