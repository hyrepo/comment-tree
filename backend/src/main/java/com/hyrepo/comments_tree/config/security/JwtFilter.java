package com.hyrepo.comments_tree.config.security;

import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.exception.InvalidTokenException;
import com.hyrepo.comments_tree.exception.UserNotFoundException;
import com.hyrepo.comments_tree.service.JwtService;
import com.hyrepo.comments_tree.service.SessionService;
import com.hyrepo.comments_tree.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESHED_TOKEN = "Refreshed-Token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;
    private final SessionService sessionService;

    public JwtFilter(UserDetailsService userDetailsService, JwtService jwtService, UserService userService, SessionService sessionService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = jwtService.extractUsername(token);
        User user = userService.findByUsername(username).orElseThrow(UserNotFoundException::new);
        sessionService.findLoginRecord(user.getId(), token).orElseThrow(() -> new InvalidTokenException("Token expired."));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetailsService.loadUserByUsername(username),
                null,
                Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
