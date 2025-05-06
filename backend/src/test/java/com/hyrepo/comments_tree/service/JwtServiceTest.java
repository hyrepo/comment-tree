package com.hyrepo.comments_tree.service;

import com.hyrepo.comments_tree.config.security.JwtConfig;
import com.hyrepo.comments_tree.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Import(JwtService.class)
@ExtendWith(SpringExtension.class)
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;
    @MockitoBean
    private JwtConfig jwtConfig;

    @Test
    void shouldGenerateValidJwt() {
        User user = new User("username", "password", "test@test.com");

        when(jwtConfig.getSecret()).thenReturn("w3NLZqYP8HlR/ZzA7OJ5Cx5R1aNoyvZDoS8/9FJ2bwA=");
        when(jwtConfig.getExpirationMinutes()).thenReturn(10L);

        String token = jwtService.generate(user);
        Jws<Claims> parsedToken = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .build()
                .parseSignedClaims(token);
        Claims claims = parsedToken.getPayload();
        Instant issuedAt = claims.getIssuedAt().toInstant();
        Instant expiration = claims.getExpiration().toInstant();
        Instant expectedExpiration = issuedAt.plus(jwtConfig.getExpirationMinutes(), ChronoUnit.MINUTES);

        assertThat(token).isNotEmpty();
        assertThat(claims.get("username")).isEqualTo("username");
        assertThat(claims.get("email")).isEqualTo("test@test.com");
        assertThat(expiration).isEqualTo(expectedExpiration);
    }

    @Test
    void shouldReturnTrueWhenValidateTokenGivenTokenIsValid() {
        String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29t" +
                "IiwiaWF0IjoxNzQyNDczMjQzLCJleHAiOjYwMTc0MjQ3MzI0M30.lhfqJIeIECZcDXeO56njRWdOrlEacD44H6672xWrrCM";
        when(jwtConfig.getSecret()).thenReturn("w3NLZqYP8HlR/ZzA7OJ5Cx5R1aNoyvZDoS8/9FJ2bwA=");

        assertThat(jwtService.isTokenValid(validToken)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenValidateTokenGivenTokenIsInvalid() {
        String invalidToken = "xeyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29t" +
                "IiwiaWF0IjoxNzQyNDczMjQzLCJleHAiOjYwMTc0MjQ3MzI0M30.lhfqJIeIECZcDXeO56njRWdOrlEacD44H6672xWrrCM";
        when(jwtConfig.getSecret()).thenReturn("w3NLZqYP8HlR/ZzA7OJ5Cx5R1aNoyvZDoS8/9FJ2bwA=");

        assertThat(jwtService.isTokenValid(invalidToken)).isFalse();
    }

    @Test
    void shouldReturnFalseWhenValidateTokenGivenTokenIsExpired() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwiZW1haWwiOi" +
                "J0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNzQyNDc0OTg0LCJleHAiOjE3NDI0NzQ5ODR9.36W_q4N" +
                "I7l9dV5t8YNzkjNVl3aV4NNegSvgLKJMks8g";

        when(jwtConfig.getSecret()).thenReturn("w3NLZqYP8HlR/ZzA7OJ5Cx5R1aNoyvZDoS8/9FJ2bwA=");

        assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29t" +
                "IiwiaWF0IjoxNzQyNDczMjQzLCJleHAiOjYwMTc0MjQ3MzI0M30.lhfqJIeIECZcDXeO56njRWdOrlEacD44H6672xWrrCM";
        when(jwtConfig.getSecret()).thenReturn("w3NLZqYP8HlR/ZzA7OJ5Cx5R1aNoyvZDoS8/9FJ2bwA=");

        assertThat(jwtService.extractUsername(validToken)).isEqualTo("username");
    }
}