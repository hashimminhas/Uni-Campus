package com.unicampus.student.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final String SECRET = "unicampus-jwt-secret-key-for-cp3-2026-abcdef123456";
    private static final long EXPIRY_MS = 86400000L; // 24 hours

    private SecretKey key() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(UUID studentId) {
        return Jwts.builder()
                .subject(studentId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(key())
                .compact();
    }

    public String extractStudentId(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
