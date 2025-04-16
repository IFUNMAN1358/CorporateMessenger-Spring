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
public class AspectLoggingDomainService {

    @Pointcut("execution(* com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.*.*(..))")
    public void cassandraDomainServices() {};

    @Pointcut("execution(* com.nagornov.CorporateMessenger.domain.service.domainService.jpa.*.*(..))")
    public void jpaDomainServices() {};

    @Pointcut("execution(* com.nagornov.CorporateMessenger.domain.service.domainService.minio.*.*(..))")
    public void minioDomainServices() {};

    @Pointcut("execution(* com.nagornov.CorporateMessenger.domain.service.domainService.redis.*.*(..))")
    public void redisDomainServices() {};

    @Pointcut("execution(* com.nagornov.CorporateMessenger.domain.service.domainService.security.*.*(..))")
    public void securityDomainServices() {};


    @Around("cassandraDomainServices() || jpaDomainServices() || minioDomainServices() || redisDomainServices() || securityDomainServices()")
    public Object logDomainServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        AspectCallerManager.saveCallerDataToMDC(joinPoint);

        try {

            log.info(
                    "DomService {}{} started",
                    joinPoint.getSignature().getName(),
                    joinPoint.getArgs()
            );

            Object result = joinPoint.proceed();

            log.info(
                    "DomService {}{} finished",
                    joinPoint.getSignature().getName(),
                    result == null ? null : result.toString()
            );

            return result;

        } catch (Exception e) {
            log.error("DomService failed", e);
            throw e;
        } finally {
            AspectCallerManager.clearCallerData();
        }

    }

}