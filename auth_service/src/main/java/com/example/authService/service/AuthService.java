package com.example.authService.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authService.model.AppUser;
import com.example.authService.repository.UserRepository;
import com.example.authService.security.JwtUtils;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtils jwt;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtUtils jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public void register(String name, String email, String rawPwd) {
        repo.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("email in use");
        });
        repo.save(new AppUser(name, email, encoder.encode(rawPwd)));
    }

    @Transactional
    public AuthTokens login(String email, String rawPwd, boolean staySignedIn) {
        AppUser u = repo.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        if (!encoder.matches(rawPwd, u.getPasswordHash()))
            throw new BadCredentialsException("Bad credentials");

        String access = jwt.generateToken(u);
        String refresh = null;

        if (staySignedIn) {
            refresh = jwt.generateRefreshToken(email);
            u.setRefreshToken(refresh);
        }

        return new AuthTokens(access, refresh);
    }

    public String refresh(String accessToken) {
        String email = jwt.getUsernameFromExpiredToken(accessToken);

        AppUser user = repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("invalid user"));

        String dbRefreshToken = user.getRefreshToken();
        if (dbRefreshToken == null) {
            throw new IllegalArgumentException("refresh revoked");
        }

        return jwt.generateToken(user);
    }

    public void logout(String accessToken) {
        try {
            String email = jwt.getUsernameFromJwt(accessToken);
            repo.findByEmail(email).ifPresent(user -> {
                user.setRefreshToken(null);
                repo.save(user);
            });
        } catch (Exception e) {
            // Silently ignore if token is invalid or user not found
            System.out.println("Logout skipped: " + e.getMessage());
        }
    }



    public static class AuthTokens {
        private final String access;
        private final String refresh;

        public AuthTokens(String access, String refresh) {
            this.access = access;
            this.refresh = refresh;
        }

        public String access() {
            return access;
        }

        public String refresh() {
            return refresh;
        }
    }
}
