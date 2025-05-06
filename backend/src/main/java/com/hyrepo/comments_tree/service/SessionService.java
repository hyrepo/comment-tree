package com.hyrepo.comments_tree.service;

import com.hyrepo.comments_tree.model.entity.LoginRecord;
import com.hyrepo.comments_tree.model.entity.User;
import com.hyrepo.comments_tree.exception.UserNotFoundException;
import com.hyrepo.comments_tree.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserService userService;

    public SessionService(SessionRepository sessionRepository, UserService userService) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
    }


    public void saveLoginRecord(User user, String token) {
        sessionRepository.save(new LoginRecord(user.getId(), Instant.now().getEpochSecond(), token));
    }

    @Transactional
    public void logout(String username, String token) {
        User user = userService.findByUsername(username).orElseThrow(UserNotFoundException::new);
        sessionRepository.deleteByUserIdAndToken(user.getId(), token);
    }

    public Optional<LoginRecord> findLoginRecord(long userId, String token) {
        return sessionRepository.findByUserIdAndToken(userId, token);
    }

}
