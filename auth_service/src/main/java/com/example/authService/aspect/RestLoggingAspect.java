package com.example.authService.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RestLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RestLoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllers() {}

    @Around("restControllers()")
    public Object logRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.info("➡️ {}", method);

        try {
            Object result = joinPoint.proceed();
            log.info("✅ {} completed", method);
            return result;
        } catch (Exception ex) {
            log.error("❌ {} fxxailed: {}", method, ex);
            throw ex;
        }
    }
}
