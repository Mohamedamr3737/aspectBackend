package com.example.review_service.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut for all controller and service methods
    @Pointcut("execution(* com.example.review_service.controller..*(..)) || execution(* com.example.review_service.service..*(..))")
    public void appMethods() {}

    @Before("appMethods()")
    public void logMethodEntry(JoinPoint joinPoint) {
        logger.info("➡️ Entering: {} with arguments = {}", 
            joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "appMethods()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        logger.info("✅ Exiting: {} with result = {}", 
            joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "appMethods()", throwing = "ex")
    public void logMethodException(JoinPoint joinPoint, Throwable ex) {
        logger.error("❌ Exception in: {} with message = {}", 
            joinPoint.getSignature(), ex.getMessage());
    }

    // Optional: log execution time
    @Around("appMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object output = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        logger.info("⏱ Execution time: {} took {} ms", 
            joinPoint.getSignature(), duration);
        return output;
    }
}
