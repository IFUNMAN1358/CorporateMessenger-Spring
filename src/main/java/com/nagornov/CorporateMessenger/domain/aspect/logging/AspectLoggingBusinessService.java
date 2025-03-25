package com.nagornov.CorporateMessenger.domain.aspect.logging;

import com.nagornov.CorporateMessenger.domain.aspect.utils.AspectCallerManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AspectLoggingBusinessService {

    @Pointcut("execution(* com.nagornov.CorporateMessenger.domain.service.businessService.cassandra.*.*(..))")
    public void cassandraBusinessServices() {};


    @Around("cassandraBusinessServices()")
    public Object logBusinessServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        AspectCallerManager.saveCallerDataToMDC(joinPoint);

        try {

            log.info("BusService started");

            Object result = joinPoint.proceed();

            log.info("BusService finished");

            return result;

        } catch (Exception e) {
            log.error("BusService failed", e);
            throw e;
        } finally {
            AspectCallerManager.clearCallerData();
        }

    }

}
