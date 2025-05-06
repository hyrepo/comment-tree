package com.hyrepo.comments_tree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyrepo.comments_tree.config.security.JwtFilter;
import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.model.dto.UserRequest;
import com.hyrepo.comments_tree.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void shouldGetBadRequestResponseWhenRegisterWithInvalidUsername() throws Exception {
        UserRequest invalidUser = new UserRequest("", "Valid-password-0", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest(null, "Valid-password-0", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest("", "Valid-password-0", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest("test", "Valid-password-0", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest("usernameWhichIsTooLong", "Valid-password-0", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest("user-name", "Valid-password-0", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetConflictResponseWhenRegisterWithDuplicatedUsername() throws Exception {
        UserRequest duplicatedUser = new UserRequest("testuser", "Valid-password-0", "teste@test.com");

        User userInDb = new User(1L, duplicatedUser.username(), duplicatedUser.password(), duplicatedUser.email());
        when(userService.findByUsername(duplicatedUser.username()))
                .thenReturn(Optional.of(userInDb));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatedUser)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetBadRequestResponseWhenRegisterWithInvalidPassword() throws Exception {
        UserRequest invalidUser = new UserRequest("validUsername", "", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());


        invalidUser = new UserRequest("validUsername", null, "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest("validUsername", "short", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest("validUsername", "password-which-is-too-long", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest("validUsername", "invalid-Pattern", "teste@test.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetBadRequestResponseWhenRegisterWithInvalidEmail() throws Exception {
        UserRequest invalidUser = new UserRequest("validUsername", "Valid-password-0", null);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest("validUsername", "Valid-password-0", "");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        invalidUser = new UserRequest("validUsername", "Valid-password-0", "test@");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetConflictResponseWhenRegisterWithDuplicatedEmail() throws Exception {
        UserRequest duplicatedUser = new UserRequest("testuser", "Valid-password-0", "teste@test.com");

        User userInDb = new User(1L, duplicatedUser.username(), duplicatedUser.password(), duplicatedUser.email());
        when(userService.findByEmail(duplicatedUser.email()))
                .thenReturn(Optional.of(userInDb));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatedUser)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetOkResponseWhenRegisterWithValidInfo() throws Exception {
        UserRequest validUser = new UserRequest("testuser", "Valid-password-0", "teste@test.com");

        when(userService.save(any())).thenReturn(new User(validUser.username(), validUser.password(), validUser.email()));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).save(any());
    }
}
