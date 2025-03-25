package com.nagornov.CorporateMessenger.domain.aspect.logging;

import com.nagornov.CorporateMessenger.domain.aspect.utils.AspectCallerManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class AspectLoggingController {

    @Around("execution(* com.nagornov.CorporateMessenger.application.controller.*.*(..))")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        AspectCallerManager.saveCallerDataToMDC(joinPoint);

        try {

            log.info("Controller started | {}", controllerArgsFormatter(joinPoint));

            Object result = joinPoint.proceed();

            log.info("Controller finished");

            return result;

        } catch (Exception e) {
            log.error("Controller failed", e);
            throw e;
        } finally {
            AspectCallerManager.clearCallerData();
        }
    }

    private static String controllerArgsFormatter(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "No arguments";
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Annotation[] annotations = parameterAnnotations[i];
            String argType = determineArgType(annotations, parameterTypes[i]);

            if (!result.isEmpty()) {
                result.append(", ");
            }

            switch (argType) {
                case "RequestBody":
                    result.append("RequestBody=").append(arg);
                    break;
                case "PathVariable":
                    result.append("PathVariable=").append(arg);
                    break;
                case "RequestParam":
                    result.append("RequestParam=").append(arg);
                    break;
                case "RequestHeader":
                    result.append("RequestHeader=").append(arg);
                    break;
                case "ModelAttribute":
                    result.append("ModelAttribute=").append(arg);
                    break;
                case "CookieValue":
                    result.append("CookieValue=").append(arg);
                    break;
                default:
                    result.append("Arg=").append(arg);
            }
        }
        return result.toString();
    }

    private static String determineArgType(Annotation[] annotations, Class<?> paramType) {
        return Arrays.stream(annotations)
                .map(annotation -> {
                    if (annotation instanceof RequestBody) return "RequestBody";
                    if (annotation instanceof PathVariable) return "PathVariable";
                    if (annotation instanceof RequestParam) return "RequestParam";
                    if (annotation instanceof RequestHeader) return "RequestHeader";
                    if (annotation instanceof ModelAttribute) return "ModelAttribute";
                    if (annotation instanceof CookieValue) return "CookieValue";
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> {
                    if (paramType.equals(HttpServletRequest.class)) return "HttpServletRequest";
                    if (paramType.equals(HttpServletResponse.class)) return "HttpServletResponse";
                    if (paramType.equals(Principal.class)) return "Principal";
                    return "Unknown";
                });
    }
}
