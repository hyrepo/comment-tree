package com.hyrepo.comments_tree.repository;

import com.hyrepo.comments_tree.model.entity.LoginRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<LoginRecord, Long> {
    void deleteByUserIdAndToken(Long userId, String token);

    Optional<LoginRecord> findByUserIdAndToken(Long userId, String token);
}
