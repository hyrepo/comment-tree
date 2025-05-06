package com.hyrepo.comments_tree.controller;

import com.hyrepo.comments_tree.exception.UserNotFoundException;
import com.hyrepo.comments_tree.model.dto.CommentRequest;
import com.hyrepo.comments_tree.model.dto.CommentResponse;
import com.hyrepo.comments_tree.model.entity.Comment;
import com.hyrepo.comments_tree.service.CommentService;
import com.hyrepo.comments_tree.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/comments")
@Tag(name = "CommentController", description = "Provide comment related function")
public class CommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> postComment(@Valid @RequestBody CommentRequest comment) {
        LOGGER.info("Creating comment for user {}, parent comment ID: {}", comment.username(), comment.parentId());

        Comment savedComment = commentService.save(comment);
        String username = userService.findByUsername(comment.username()).orElseThrow(UserNotFoundException::new).getUsername();

        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentResponse(savedComment.getId(),
                username, savedComment.getContent(), savedComment.getCreatedAt(), comment.parentId(), Collections.emptyList()));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        return ResponseEntity.ok(commentService.findAll());
    }
}
