package com.example.authService.dto;

public record SigninDto(String email, String password, boolean staySignedIn) {}
