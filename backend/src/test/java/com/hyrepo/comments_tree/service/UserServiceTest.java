package com.hyrepo.comments_tree.service;

import com.hyrepo.comments_tree.config.BeanConfig;
import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import({UserService.class, BeanConfig.class})
@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldReturnEmptyWhenFindByUsernameGivenNoSuchUserExist() {
        String username = "testUsername";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThat(userService.findByUsername(username).isEmpty()).isTrue();
    }

    @Test
    void shouldReturnUserWhenFindByUsernameGivenSuchUserExist() {
        String username = "testUsername";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User(username, "password", "test@test.com")));

        Optional<User> user = userService.findByUsername(username);
        assertThat(user.isPresent()).isTrue();
        assertThat(user.get().getUsername()).isEqualTo(username);
    }

    @Test
    void shouldReturnEmptyWhenFindByEmailGivenNoSuchUserExist() {
        String email = "test@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThat(userService.findByEmail(email).isEmpty()).isTrue();
    }

    @Test
    void shouldReturnUserWhenFindByEmailGivenSuchUserExist() {
        String email = "test@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User("username", "password", email)));

        Optional<User> user = userService.findByEmail(email);
        assertThat(user.isPresent()).isTrue();
        assertThat(user.get().getEmail()).isEqualTo(email);
    }

    @Test
    void shouldSaveUserAndEncryptPassword() {
        User user = new User("username", "password", "test@test.com");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        userService.save(user);
        verify(userRepository).save(captor.capture());
        User encryptedUser = captor.getValue();

        assertThat(encryptedUser.getPassword()).isNotEqualTo(user.getPassword());
        assertThat(passwordEncoder.matches(user.getPassword(), encryptedUser.getPassword())).isTrue();
    }

    @Test
    void shouldFindUserByEmailOfUsername() {
        String email = "test@test.com";
        when(userRepository.findByUsername(email)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User("username", "password", email)));

        Optional<User> user = userService.findByUsernameOrEmail(email);
        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo("username");
        assertThat(user.get().getEmail()).isEqualTo(email);
    }

    @Test
    void shouldFindUserById() {
        User user = new User(1L, "username", "password", "test@test.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Optional<User> userInDb = userService.findById(1L);

        assertThat(userInDb.isPresent()).isTrue();
        assertThat(userInDb.get().getUsername()).isEqualTo(user.getUsername());

    }
}