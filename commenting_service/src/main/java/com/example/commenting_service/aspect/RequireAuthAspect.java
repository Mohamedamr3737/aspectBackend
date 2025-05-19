package com.example.commenting_service.aspect;


import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.commenting_service.annotation.RequireAuth;
import com.example.commenting_service.exception.ForbiddenException;
import com.example.commenting_service.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Aspect @Component
public class RequireAuthAspect {

    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(RequireAuthAspect.class);

    public RequireAuthAspect(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Around("@annotation(requireAuth)")
    public Object check(ProceedingJoinPoint jp, RequireAuth requireAuth) throws Throwable {

        HttpServletRequest req =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        UUID userId = jwtUtil.extractUserId(req);
        if (userId == null) {
            log.warn("❌ Unauthorized -> {}", jp.getSignature());
            throw new ForbiddenException("JWT required");
        }
        req.setAttribute("userId", userId);
        return jp.proceed();
    }
}
