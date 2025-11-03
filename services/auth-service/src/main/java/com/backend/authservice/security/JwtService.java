package com.backend.authservice.security;

import com.backend.authservice.entity.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Component
public class JwtService {
    private final SecretKey key;
    private final long jwtExpirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes:15}") long jwtExpirationMinutes){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMinutes = jwtExpirationMinutes;
    }

    public String generateToken(UserAccount userAccount){
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtExpirationMinutes * 60);
        return Jwts.builder()
                .subject(userAccount.getId().toString())
                //.claim("email", userAccount.getEmail())   //Thêm email để hiển thị hoặc audit
                .claim("role", userAccount.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

//
//    public String extractEmail(String token) {
//        return parse(token).getPayload().getSubject();
//    }

    public UUID extractUserId(String token) {
        try {
            return UUID.fromString(parse(token).getPayload().getSubject());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid userId format in JWT", e);
        }
    }

    public String extractEmail(String token) {
        return parse(token).getPayload().get("email", String.class);
    }

    public String extractRole(String token) {
        return parse(token).getPayload().get("role", String.class);
    }

}
