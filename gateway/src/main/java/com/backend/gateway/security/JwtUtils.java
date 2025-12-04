package com.backend.gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    private final SecretKey key;

    public JwtUtils(@Value("${app.jwt.secret}") String secret){
        // dùng cùng secret với auth-service
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    //Parse token và trả về Claims (payload)
    private Claims parseClaims(String token){
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        return jws.getPayload();
    }

    //Kiểm tra token còn hạn và hợp lệ hay không
    public boolean isTokenValid(String token){
        try{
            Claims claims = parseClaims(token);
            Date exp = claims.getExpiration();
            return exp == null || exp.after(new Date());
        } catch (ExpiredJwtException ex){ //het han
            return false;
        } catch (Exception e) { //sai signature, format lỗi
            return false;
        }
    }

    public UUID extractUserId(String token){
        Claims claims = parseClaims(token);
        String sub = claims.getSubject();

        return UUID.fromString(sub);
    }

    public String extractRole(String token){
        Claims claims = parseClaims(token);

        return claims.get("role", String.class);
    }

    public String extractEmail(String token) {
        Claims claims = parseClaims(token);

        return claims.get("email", String.class);
    }
}
