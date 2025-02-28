package com.nagornov.CorporateMessenger.domain.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AspectLoggingApplicationService {

    @Around("execution(* com.nagornov.CorporateMessenger.application.applicationService.*.*(..))")
    public Object logApplicationServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();

        try {

            log.info("AppService {} started", methodName);

            Object result = joinPoint.proceed();

            log.info("AppService {} finished", methodName);

            return result;

        } catch (Exception e) {
            log.error("AppService {} failed", methodName, e);
            throw e;
        }
    }

}
