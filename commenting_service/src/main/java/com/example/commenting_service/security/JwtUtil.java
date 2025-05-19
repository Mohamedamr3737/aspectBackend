package com.example.commenting_service.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

    private final String secret = "supersecretkey12345678901234567890123";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    public UUID extractUserId(HttpServletRequest request) {
        String token = getCookie(request, "jwt");
        if (token == null) return null;

        Claims claims = parseToken(token);
        String userIdStr = claims.get("userId", String.class);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    public String extractUsername(HttpServletRequest request) {
        String token = getCookie(request, "jwt");
        if (token == null) return null;

        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public String extractEmail(HttpServletRequest request) {
        String token = getCookie(request, "jwt");
        if (token == null) return null;

        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    public String extractRole(HttpServletRequest request) {
        String token = getCookie(request, "jwt");
        if (token == null) return null;

        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String getCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
