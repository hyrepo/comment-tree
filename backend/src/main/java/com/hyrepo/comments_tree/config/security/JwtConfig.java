package com.hyrepo.comments_tree.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private Long expirationMinutes;

    public JwtConfig() {
    }

    public String getSecret() {
        return secret;
    }

    public Long getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setExpirationMinutes(Long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }
}
