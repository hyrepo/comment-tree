package com.hyrepo.comments_tree.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyrepo.comments_tree.model.dto.*;
import com.hyrepo.comments_tree.util.DateTimeUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled   // remove before run this test
public class CommentIPerformanceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentIPerformanceTest.class);
    private static final String LOCALHOST = "http://localhost:";
    private static final int MAX_TIME_COST_IN_MS = 500;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    /**
     * Remove the @Disabled annotation before running.
     *
     * Test logic:
     * Insert 100 * 100 comments (100 top level comments, depth for each is 100),
     * then read the whole comment tree, check if the tree rendering time cost
     * is less than MAX_TIME_COST_IN_MS(default value is 500ms)
     */
    @Test
    void shouldRetrieveHugeNumberOfCommentsWithoutAnyPerformanceIssue() {
        String url = LOCALHOST + port;
        UserRequest userRequest = new UserRequest("user1", "Valid-password-0", "teste@test.com");

        register(userRequest);

        String token = login(userRequest);

        int topCommentCount = 100;
        int commentDepth = 100;
        List<Integer> topComments = IntStream.rangeClosed(1, topCommentCount).boxed().toList();
        LOGGER.info("Creating {} comments", topCommentCount * commentDepth);
        topComments.parallelStream().forEach((it) -> {
            Long parentId = null;
            for (var subComment = 0; subComment < commentDepth; subComment++) {
                ResponseEntity<CommentResponse> commentResponse = postComment(token, userRequest.username(), parentId);

                parentId = commentResponse.getBody().getId();
            }
        });
        LOGGER.info("Creating finished, rendering tree.");

        long startTimestamp = DateTimeUtil.now();
        ResponseEntity<List<CommentResponse>> allCommentsResponse =
                restTemplate.exchange(url + "/comments", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        long finishTimestamp = DateTimeUtil.now();

        assertThat(allCommentsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(finishTimestamp - startTimestamp < MAX_TIME_COST_IN_MS).isTrue();
        LOGGER.info("Time cost: {}ms", finishTimestamp - startTimestamp);

        List<CommentResponse> allComments = allCommentsResponse.getBody();
        assertThat(allComments.size()).isEqualTo(topCommentCount);
    }

    private String login(UserRequest userRequest) {
        String url = LOCALHOST + port;
        LoginRequest loginRequest = new LoginRequest(userRequest.username(), userRequest.password());
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(url + "/sessions", loginRequest, LoginResponse.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        return loginResponse.getBody().token();
    }

    private ResponseEntity<CommentResponse> postComment(String token, String username, Long parentId) {
        String url = LOCALHOST + port;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        String content = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        CommentRequest commentRequest = new CommentRequest(username, content, parentId);
        HttpEntity<CommentRequest> commentRequestHttpEntity = new HttpEntity<>(commentRequest, headers);

        ResponseEntity<CommentResponse> commentResponse = restTemplate.exchange(url + "/comments", HttpMethod.POST, commentRequestHttpEntity, CommentResponse.class);

        assertThat(commentResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return commentResponse;
    }

    private void register(UserRequest userRequest) {
        String url = LOCALHOST + port;
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(url + "/users", userRequest, String.class);
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
