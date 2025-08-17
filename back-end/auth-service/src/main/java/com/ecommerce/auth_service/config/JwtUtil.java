package com.ecommerce.auth_service.config;

import com.ecommerce.auth_service.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final long expirationInHours = 60 * 60 * 1000;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * expirationInHours))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Long validateAndGetUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public long getExpiration() {
        return 24 * expirationInHours;
    }
}

