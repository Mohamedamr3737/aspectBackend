package com.example.review_service.aspect;

import com.example.review_service.annotation.RequireAuth;
import com.example.review_service.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Around;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Aspect
@Component
public class RequireAuthAspect {

    private final JwtUtil jwtUtil;

    public RequireAuthAspect(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Around("@annotation(requireAuth)")
    public Object checkAuth(ProceedingJoinPoint joinPoint, RequireAuth requireAuth) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        UUID userId = jwtUtil.extractUserId(request);
        if (userId == null) {
            throw new RuntimeException("Unauthorized: No valid JWT token found.");
        }

        // Inject user ID into the request for later use
        request.setAttribute("userId", userId);

        return joinPoint.proceed();
    }
}
