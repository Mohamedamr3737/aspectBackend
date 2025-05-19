package com.example.authService.aspect;
import java.time.Duration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.authService.annotation.RateLimit;
import com.example.authService.exception.RateLimitExceededException;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RateLimitAspect2 {

    private final StringRedisTemplate redisTemplate;

    public RateLimitAspect2(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(rateLimit)")
    public Object limit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        String key = "rate:" + uri + ":" + ip;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(rateLimit.durationSeconds()));
        }

        if (count != null && count > rateLimit.limit()) {
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        }

        return joinPoint.proceed();
    }
}
