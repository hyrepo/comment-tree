package com.hyrepo.comments_tree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyrepo.comments_tree.config.security.JwtFilter;
import com.hyrepo.comments_tree.model.entity.Comment;
import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.model.dto.CommentRequest;
import com.hyrepo.comments_tree.model.dto.CommentResponse;
import com.hyrepo.comments_tree.service.CommentService;
import com.hyrepo.comments_tree.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.hyrepo.comments_tree.util.DateTimeUtil.now;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private JwtFilter jwtFilter;
    @MockitoBean
    private CommentService commentService;
    @MockitoBean
    private UserService userService;

    @Test
    void shouldReturnBadRequestWhenPostCommentGivenCommentIsInvalid() throws Exception {
        CommentRequest invalidComment = new CommentRequest("username", "ab", null);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnCreatedWhenPostCommentGivenCommentIsValid() throws Exception {
        String username = "username";
        CommentRequest validComment = new CommentRequest(username, "abcd", null);


        when(userService.findByUsername(username)).thenReturn(Optional.of(new User(1L, username, "password", "test@test.com")));
        when(commentService.save(validComment)).thenReturn(new Comment(2L, username, "abcd", now()));

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.content").value("abcd"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(commentService, times(1)).save(validComment);
    }

    @Test
    void shouldReturnComments() throws Exception {
        CommentResponse comment1_1 = new CommentResponse(2, "user1", "comment 1_1", now() - 2, null, Collections.emptyList());
        CommentResponse comment1 = new CommentResponse(1, "user2", "comment 1", now() - 1, null, List.of(comment1_1));
        CommentResponse comment2 = new CommentResponse(3, "user3", "comment 2", now(), null, Collections.emptyList());

        when(commentService.findAll()).thenReturn(List.of(comment2, comment1));

        mockMvc.perform(get("/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[1].id").value(1))
                .andExpect(jsonPath("$[1].comments[0].id").value(2));
    }
}