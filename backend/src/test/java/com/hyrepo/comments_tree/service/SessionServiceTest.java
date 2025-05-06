package com.hyrepo.comments_tree.service;

import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Import(SessionService.class)
@ExtendWith(SpringExtension.class)
class SessionServiceTest {
    @Autowired
    private SessionService sessionService;
    @MockitoBean
    private SessionRepository sessionRepository;
    @MockitoBean
    private UserService userService;

    @Test
    void shouldSaveLoginRecordToDb() {
        User user = new User(1, "username", "password", "test@test.com");
        sessionService.saveLoginRecord(user, "test-token");

        verify(sessionRepository, times(1)).save(any());
    }

    @Test
    void shouldDeleteRecordFromDbWhenLogOutSuccess() {
        User user = new User(1, "username", "password", "test@test.com");
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        sessionService.logout(user.getUsername(), "test-token");

        verify(sessionRepository, times(1)).deleteByUserIdAndToken(user.getId(),"test-token");
    }

    @Test
    void shouldFindLoginRecordByUserIdAndToken() {
        User user = new User(1, "username", "password", "test@test.com");
        String token = "test-token";
        sessionService.findLoginRecord(user.getId(), token);

        verify(sessionRepository, times(1)).findByUserIdAndToken(user.getId(), token);
    }
}