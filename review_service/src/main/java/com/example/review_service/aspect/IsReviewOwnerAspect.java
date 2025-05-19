package com.example.review_service.aspect;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.review_service.exception.BadRequestException;
import com.example.review_service.exception.ForbiddenException;
import com.example.review_service.security.JwtUtil;
import com.example.review_service.service.ReviewService;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class IsReviewOwnerAspect {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    public IsReviewOwnerAspect(ReviewService reviewService, JwtUtil jwtUtil) {
        this.reviewService = reviewService;
        this.jwtUtil = jwtUtil;
    }

@Before("@annotation(com.example.review_service.aspect.IsReviewOwner)")
public void checkOwnership(JoinPoint joinPoint) {
    HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    UUID userId = jwtUtil.extractUserId(request);
    if (userId == null)
        throw new ForbiddenException("Unauthorized: No userId in request.");

    // More reliable: match by type UUID
    Object[] args = joinPoint.getArgs();
    UUID reviewId = null;
    for (Object arg : args) {
        if (arg instanceof UUID) {
            reviewId = (UUID) arg;
            break;
        }
    }

    if (reviewId == null)
        throw new BadRequestException("Missing review ID to validate ownership.");

    // Check if user is owner
    if (!reviewService.isReviewOwner(userId, reviewId)) {
        throw new ForbiddenException("Forbidden: You do not own this review.");
    }
}

}
