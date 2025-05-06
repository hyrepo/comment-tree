package com.hyrepo.comments_tree.service;

import com.hyrepo.comments_tree.config.security.JwtConfig;
import com.hyrepo.comments_tree.exception.InvalidTokenException;
import com.hyrepo.comments_tree.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    public static final String USERNAME_KEY = "username";
    public static final String EMAIL_KEY = "email";
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);
    private final JwtConfig jwtConfig;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generate(User user) {
        var now = Instant.now();
        return Jwts.builder()
                .claims()
                .add(USERNAME_KEY, user.getUsername())
                .add(EMAIL_KEY, user.getEmail())
                .and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtConfig.getExpirationMinutes(), ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            parse(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String extractUsername(String token) {
        return (String) parse(token).get(USERNAME_KEY);
    }

    private Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            LOGGER.error("Token {} is invalid, error: {}", token, e.getMessage());
            throw new InvalidTokenException(e.getMessage());
        }
    }
}
