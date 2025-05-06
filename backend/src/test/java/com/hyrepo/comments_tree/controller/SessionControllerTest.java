package com.hyrepo.comments_tree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.model.dto.LoginRequest;
import com.hyrepo.comments_tree.service.JwtService;
import com.hyrepo.comments_tree.service.SessionService;
import com.hyrepo.comments_tree.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SessionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldReturnOkAndSaveTheLoginRecordWhenLoginWithCorrectUsernameOrEmailAndPassword() throws Exception {
        String email = "teste@test.com";
        String username = "testuser";
        LoginRequest validLogin = new LoginRequest(username, "Valid-password-0");

        when(jwtService.generate(any())).thenReturn("test-jwt-token");
        when(userService.findByUsernameOrEmail(any())).thenReturn(Optional.of(new User(username, "xxx", email)));

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").value("test-jwt-token"))
                .andExpect(jsonPath("user.username").value(username))
                .andExpect(jsonPath("user.email").value(email));

        validLogin = new LoginRequest(email, "Valid-password-0");
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").value("test-jwt-token"))
                .andExpect(jsonPath("user.username").value(username))
                .andExpect(jsonPath("user.email").value(email));

        verify(sessionService, times(2)).saveLoginRecord(any(), any());
    }

    @Test
    void shouldReturnOkAndDeleteTheJwtWhenLogoutSuccess() throws Exception {

        User user = new User(1, "username", "password", "test@test.com");
        String token = "test-token";
        when(jwtService.extractUsername(token)).thenReturn(user.getUsername());
        when(userService.findByUsername("username")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/sessions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).logout(user.getUsername(), token);
    }
}
