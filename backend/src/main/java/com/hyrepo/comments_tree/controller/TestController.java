package com.hyrepo.comments_tree.controller;

import com.hyrepo.comments_tree.model.dto.CommentRequest;
import com.hyrepo.comments_tree.model.dto.CommentResponse;
import com.hyrepo.comments_tree.model.dto.LoginRequest;
import com.hyrepo.comments_tree.model.dto.UserRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.IntStream;

import static com.hyrepo.comments_tree.util.Constants.COMMENT_LENGTH_MAX;
import static com.hyrepo.comments_tree.util.DateTimeUtil.now;

@RestController
@RequestMapping("/tests")
@Tag(name = "TestController", description = "Provide APIs to setup test data")
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private CommentController commentController;
    @Autowired
    private UserController userController;
    @Autowired
    private SessionController sessionController;

    @PostMapping("/comments")
    public ResponseEntity<String> addComments(@RequestParam(defaultValue = "100")
                                              @Parameter(description = "Indicate the number of top level comments to create")
                                              int topLevelCommentCount,
                                              @RequestParam(defaultValue = "100")
                                              @Parameter(description = "Indicate the depth of each top level comments")
                                              int depth) throws Exception {
        UserRequest userRequest = new UserRequest("user1", "Valid-password-0", "teste@test.com");
        try {
            userController.register(userRequest);
        } catch (Exception e) {
            sessionController.login(new LoginRequest(userRequest.username(), userRequest.password()));
        }


        List<Integer> topComments = IntStream.rangeClosed(1, topLevelCommentCount).boxed().toList();

        LOGGER.info("Creating {} comments", topLevelCommentCount * depth);
        long startTime = now();
        topComments.parallelStream().forEach((it) -> {
            Long parentId = null;
            for (var subComment = 0; subComment < depth; subComment++) {
                ResponseEntity<CommentResponse> commentResponse = commentController.postComment(
                        new CommentRequest(userRequest.username(), RandomStringUtils.randomAlphanumeric(COMMENT_LENGTH_MAX), parentId));

                parentId = commentResponse.getBody().getId();
            }
        });
        String msg = "%s comments created, time cost: %dms".formatted(topLevelCommentCount * depth, now() - startTime);
        LOGGER.info(msg);

        return ResponseEntity.ok(msg);
    }
}
