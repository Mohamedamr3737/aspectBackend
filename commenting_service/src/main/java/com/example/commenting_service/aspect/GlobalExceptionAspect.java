package com.example.commenting_service.aspect;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.example.commenting_service.exception.BadRequestException;
import com.example.commenting_service.exception.ConflictException;
import com.example.commenting_service.exception.ForbiddenException;
import com.example.commenting_service.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class GlobalExceptionAspect {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionAspect.class);

    /* match every public controller method ONLY */
    @Pointcut("execution(public * com.example.commenting_service.controller..*(..))")
    public void controllers() {}


@Around("controllers()")
public Object wrap(ProceedingJoinPoint jp) {
    HttpServletRequest req =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    UUID user = (UUID) req.getAttribute("userId");   // may be null

    try {
        return jp.proceed(); // normal flow
    } catch (Throwable ex) {
        HttpStatus status;

        // 🔥 Add this condition
        if (ex instanceof ResponseStatusException rse) {
            status = (HttpStatus) rse.getStatusCode();
        } else {
            status =
                (ex instanceof BadRequestException)  ? HttpStatus.BAD_REQUEST  :
                (ex instanceof ForbiddenException)   ? HttpStatus.FORBIDDEN    :
                (ex instanceof ConflictException)    ? HttpStatus.CONFLICT     :
                (ex instanceof NotFoundException)    ? HttpStatus.NOT_FOUND    :
                                                        HttpStatus.INTERNAL_SERVER_ERROR;
        }

        log.error("⚠️  {} | path={} | user={} | {}", status.value(),
                  req.getRequestURI(), user, ex.getMessage(), ex);

        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   ex.getMessage());

        return ResponseEntity.status(status).body(body);
    }
}

}
