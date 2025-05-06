package com.hyrepo.comments_tree.controller;

import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.model.UserInfo;
import com.hyrepo.comments_tree.model.dto.LoginRequest;
import com.hyrepo.comments_tree.model.dto.LoginResponse;
import com.hyrepo.comments_tree.service.JwtService;
import com.hyrepo.comments_tree.service.SessionService;
import com.hyrepo.comments_tree.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@Tag(name = "SessionController", description = "Provide login/logout related function")
public class SessionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final UserService userService;

    public SessionController(AuthenticationManager authenticationManager, JwtService jwtService, SessionService sessionService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.principal(), loginRequest.password()));
        User user = userService.findByUsernameOrEmail(loginRequest.principal()).get();
        String token = jwtService.generate(user);
        sessionService.saveLoginRecord(user, token);

        LOGGER.info("User {} login success.", user.getUsername());

        return ResponseEntity.ok(new LoginResponse(token, new UserInfo(user.getUsername(), user.getEmail())));
    }

    @DeleteMapping
    @Operation(summary = "Logout")
    public ResponseEntity<String> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String jwt = token.substring(BEARER_PREFIX.length());
        String username = jwtService.extractUsername(jwt);

        sessionService.logout(username, jwt);

        LOGGER.info("User {} logout success.", username);

        return ResponseEntity.ok().build();
    }
}
