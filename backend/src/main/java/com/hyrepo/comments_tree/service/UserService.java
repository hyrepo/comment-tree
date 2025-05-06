package com.hyrepo.comments_tree.service;

import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(new User(user.getUsername(), passwordEncoder.encode(user.getPassword()), user.getEmail()));
    }

    public Optional<User> findByUsernameOrEmail(String principal) {
        return userRepository.findByUsername(principal).or(() -> userRepository.findByEmail(principal));
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
