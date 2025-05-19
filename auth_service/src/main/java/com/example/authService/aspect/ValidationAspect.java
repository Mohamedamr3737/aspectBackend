package com.example.authService.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.example.authService.dto.SigninDto;
import com.example.authService.dto.SignupDto;

@Aspect
@Component
public class ValidationAspect {

    @Before("execution(* com.example.authService.controller.AuthController.signup(..)) && args(dto,..)")
    public void validateSignup(SignupDto dto) {
        if (isBlank(dto.name()))
            throw new IllegalArgumentException("Name is required");

        if (isBlank(dto.email()) || !dto.email().contains("@"))
            throw new IllegalArgumentException("Invalid email");

        if (isBlank(dto.password()) || dto.password().length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters");
    }

    @Before("execution(* com.example.authService.controller.AuthController.signin(..)) && args(dto,..)")
    public void validateSignin(SigninDto dto) {
        if (isBlank(dto.email()))
            throw new IllegalArgumentException("Email is required");

        if (isBlank(dto.password()))
            throw new IllegalArgumentException("Password is required");
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
