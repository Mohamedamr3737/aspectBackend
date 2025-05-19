package com.example.commenting_service.aspect;

import java.lang.reflect.Parameter;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.commenting_service.exception.BadRequestException;
import com.example.commenting_service.exception.ForbiddenException;
import com.example.commenting_service.security.JwtUtil;
import com.example.commenting_service.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class IsCommentOwnerAspect {

    private final CommentService service;
    private final JwtUtil jwtUtil;

    public IsCommentOwnerAspect(CommentService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @Before("@annotation(com.example.commenting_service.aspect.IsCommentOwner)")
    public void check(JoinPoint jp) {
        HttpServletRequest req = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        UUID uid = jwtUtil.extractUserId(req);
        String role = jwtUtil.extractRole(req);
        if (uid == null) throw new ForbiddenException("Unauthorized");
        
        if ("admin".equalsIgnoreCase(role)) return;

        UUID commentId = null;
        Object[] args = jp.getArgs();
        Parameter[] params = ((MethodSignature) jp.getSignature()).getMethod().getParameters();

        for (int i = 0; i < args.length; i++) {
            if ("id".equals(params[i].getName()) && args[i] instanceof UUID uuid) {
                commentId = uuid;
                break;
            }
        }

        if (commentId == null) throw new BadRequestException("Missing commentId");

        if (!service.isCommentOwner(uid, commentId))
            throw new ForbiddenException("Not comment owner");
    }
}
