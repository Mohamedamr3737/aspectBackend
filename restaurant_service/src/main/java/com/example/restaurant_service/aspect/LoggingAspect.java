package com.example.restaurant_service.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("➡️ Entering: {}", joinPoint.getSignature());
        try {
            Object result = joinPoint.proceed();
            logger.info("✅ Completed: {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            logger.error("❌ Exception in {}: {}", joinPoint.getSignature(), e.getMessage());
            throw e;
        }
    }
}
