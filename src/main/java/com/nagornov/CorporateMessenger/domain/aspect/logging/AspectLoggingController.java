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
public class AspectLoggingController {

    @Around("execution(* com.nagornov.CorporateMessenger.application.controller.*.*(..))")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        AspectCallerManager.saveCallerDataToMDC(joinPoint);

        try {

            log.info(
                    "Controller {}{} started",
                    joinPoint.getSignature().getName(),
                    joinPoint.getArgs()
            );

            Object result = joinPoint.proceed();

            log.info(
                    "Controller {}{} finished",
                    joinPoint.getSignature().getName(),
                    result == null ? null : result.toString()
            );

            return result;

        } catch (Exception e) {
            log.error("Controller failed", e);
            throw e;
        } finally {
            AspectCallerManager.clearCallerData();
        }
    }
}
