package com.nagornov.CorporateMessenger.domain.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AspectLoggingDomainService {

    @Pointcut("execution(* com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.*.*(..))")
    public void cassandraDomainServices() {};

    @Pointcut("execution(* com.nagornov.CorporateMessenger.domain.service.domainService.jpa.*.*(..))")
    public void jpaDomainServices() {};

    @Around("cassandraDomainServices() || jpaDomainServices()")
    public Object logDomainServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();

        try {

            log.info("DomService {} started", methodName);

            Object result = joinPoint.proceed();

            log.info("DomService {} finished", methodName);

            return result;

        } catch (Exception e) {
            log.error("DomService {} failed", methodName, e);
            throw e;
        }

    }

}
