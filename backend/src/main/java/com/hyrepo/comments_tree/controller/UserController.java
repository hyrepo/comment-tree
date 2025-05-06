package com.hyrepo.comments_tree.controller;

import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.exception.DuplicateUserException;
import com.hyrepo.comments_tree.model.dto.UserRequest;
import com.hyrepo.comments_tree.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "UserController", description = "Provide user registration related function")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> register(@Valid @RequestBody UserRequest userRequest) {
        if (isUserExisted(userRequest)) {
            LOGGER.error("User {} or Email {} already exist.", userRequest.username(), userRequest.email());
            throw new DuplicateUserException();
        }
        User savedUser = userService.save(new User(userRequest.username(), userRequest.password(), userRequest.email()));

        LOGGER.info("User {} register success.", savedUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private boolean isUserExisted(UserRequest userRequest) {
        return userService.findByUsername(userRequest.username()).isPresent() ||
                userService.findByEmail(userRequest.email()).isPresent();
    }
}
