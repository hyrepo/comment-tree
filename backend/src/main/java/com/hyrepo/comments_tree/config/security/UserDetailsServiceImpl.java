package com.hyrepo.comments_tree.config.security;

import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.exception.UserNotFoundException;
import com.hyrepo.comments_tree.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String principal) {
        User user = userService.findByUsernameOrEmail(principal).orElseThrow(UserNotFoundException::new);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
