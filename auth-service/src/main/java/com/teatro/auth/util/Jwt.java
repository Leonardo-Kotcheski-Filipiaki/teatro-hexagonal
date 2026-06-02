package com.teatro.auth.util;

import com.teatro.auth.dto.JwtUserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class Jwt {

    // Chave lida de application.properties: jwt.secret (mínimo 256 bits)
    @Value("${jwt.secret}")
    private String secret;

    private static final Duration ACCESS_TTL  = Duration.ofMinutes(15);
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    public String generateAccessToken(JwtUserDto user) {
        return Jwts.builder()
                .subject(user.id().toString())
                .claim("role", user.role().name())
                .claim("email", user.email())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(ACCESS_TTL)))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(JwtUserDto user) {
        return Jwts.builder()
                .subject(user.id().toString())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(REFRESH_TTL)))
                .signWith(getKey())
                .compact();
    }

    // Retorna o userId extraído do token; lança JwtException se inválido
    public Long extractUserId(String token) {
        return Long.parseLong(
                Jwts.parser().verifyWith(getKey()).build()
                        .parseSignedClaims(token).getPayload().getSubject()
        );
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token).getPayload();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
