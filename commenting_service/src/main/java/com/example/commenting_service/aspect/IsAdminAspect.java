package com.example.commenting_service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.commenting_service.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class IsAdminAspect {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    public IsAdminAspect(JwtUtil jwtUtil, HttpServletRequest request) {
        this.jwtUtil = jwtUtil;
        this.request = request;
    }

    @Pointcut("@annotation(com.example.commenting_service.annotation.IsAdmin)")
    public void isAdminEndpoint() {}

    @Before("isAdminEndpoint()")
    public void checkAdmin(JoinPoint joinPoint) {
        String role = jwtUtil.extractRole(request);
        if (role == null || !role.equalsIgnoreCase("admin")) {
            throw new ResponseStatusException(UNAUTHORIZED, "Only admins are authorized to access this resource.");
        }
    }
}
