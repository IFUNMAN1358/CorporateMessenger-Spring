package com.nagornov.CorporateMessenger.domain.aspect.logging;

import com.nagornov.CorporateMessenger.domain.aspect.utils.AspectCallerManager;
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

        AspectCallerManager.saveCallerDataToMDC(joinPoint);

        try {

            log.info("AppService started");

            Object result = joinPoint.proceed();

            log.info("AppService finished");

            return result;

        } catch (Exception e) {
            log.error("AppService failed", e);
            throw e;
        } finally {
            AspectCallerManager.clearCallerData();
        }
    }

}
