package com.onhz.server.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;
import java.util.Date;

@Configuration
public class JwtConfig {
    @Value("${spring.service.jwt.secret-key}")
    private String secretKey;
    @Value("${spring.service.jwt.access-token-expiration}")
    private Long accessTokenExpiration;
    @Value("${spring.service.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String generateToken(String email, String UserName) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(email)
                .claim("username", UserName)
                .issuedAt(now)
                .expiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String generateRefreshToken(String email, String UserName) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(email)
                .claim("username", UserName)
                .issuedAt(now)
                .expiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String getEmail(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUserName(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userName", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
