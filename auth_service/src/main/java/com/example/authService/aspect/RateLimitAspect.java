package com.example.authService.aspect;

import java.time.Duration;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redis;

    public RateLimitAspect(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    private static final String ATTEMPT = "login:attempt:";
    private static final String LOCK = "login:lock:";

    @Value("${security.login.max-tries:3}")
    private int MAX_TRIES;

    @Value("${security.login.lock-sec:300}")
    private long LOCK_SEC;

    @Pointcut("execution(* com.example.authService.service.AuthService.login(..))")
    private void loginMethod() {}

    @Before("loginMethod() && args(email,..)")
    public void blockIfLocked(String email) {
        if (Boolean.TRUE.equals(redis.hasKey(LOCK + email))) {
            throw new IllegalStateException("Account locked – retry later");
        }
    }

    @AfterThrowing(pointcut = "loginMethod() && args(email,..)", throwing = "ex")
    public void onFail(String email, Throwable ex) {
        if (!(ex instanceof BadCredentialsException)) return;

        Long tries = redis.opsForValue().increment(ATTEMPT + email);
        redis.expire(ATTEMPT + email, Duration.ofMinutes(10));

        if (tries != null && tries >= MAX_TRIES) {
            redis.delete(ATTEMPT + email);
            redis.opsForValue().set(LOCK + email, "1", Duration.ofSeconds(LOCK_SEC));
        }
    }

    @AfterReturning("loginMethod() && args(email,..)")
    public void onSuccess(String email) {
        redis.delete(ATTEMPT + email);
        redis.delete(LOCK + email);
    }
}
