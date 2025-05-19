package com.example.commenting_service.aspect;

import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Aspect @Component
public class RestLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RestLoggingAspect.class);

    @PostConstruct
    public void init() { log.info("✅ RestLoggingAspect ready"); }

    @Pointcut("execution(* com.example.commenting_service.controller..*(..)) || " +
              "execution(* com.example.commenting_service.service..*(..))")
    public void app() {}

    @Around("app()")
    public Object wrap(ProceedingJoinPoint jp) throws Throwable {
        HttpServletRequest req =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        UUID uid = (UUID) req.getAttribute("userId");
        String sig = jp.getSignature().toShortString();

        log.info("➡️ {} | user={}", sig, uid);
        try {
            Object out = jp.proceed();
            log.info("✅ {} | user={}", sig, uid);
            return out;
        } catch (Throwable ex) {
            log.error("❌ {} | user={} | {}", sig, uid, ex.getMessage());
            throw ex;
        }
    }
}
