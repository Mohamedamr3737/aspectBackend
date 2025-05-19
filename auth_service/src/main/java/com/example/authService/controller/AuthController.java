package com.example.authService.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authService.annotation.RateLimit;
import com.example.authService.dto.SigninDto;
import com.example.authService.dto.SignupDto;
import com.example.authService.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/signup")
    @RateLimit(limit = 3, durationSeconds = 3600)
    public ResponseEntity<?> signup(@RequestBody SignupDto dto) {
        auth.register(dto.name(), dto.email(), dto.password());
        return ResponseEntity.ok(Map.of("message", "user created"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninDto dto, HttpServletResponse rsp) {
        AuthService.AuthTokens tokens = auth.login(dto.email(), dto.password(), dto.staySignedIn());

        // 🟢 Return token in JSON instead of setting cookie
        return ResponseEntity.ok(Map.of(
            "accessToken", tokens.access(),
            "message", "login ok"
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("jwt") String accessToken) {
        String newAccess = auth.refresh(accessToken);

        return ResponseEntity.ok(Map.of(
            "accessToken", newAccess,
            "message", "refreshed"
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("jwt") String accessToken) {
        auth.logout(accessToken);
        return ResponseEntity.ok(Map.of("message", "logged out"));
    }
}
