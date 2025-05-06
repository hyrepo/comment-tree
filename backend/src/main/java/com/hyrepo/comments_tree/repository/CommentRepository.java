package com.hyrepo.comments_tree.repository;

import com.hyrepo.comments_tree.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = """
                SELECT comments.*
                FROM comments
                JOIN comment_closure ON comment_closure.descendant_id = comments.id
                WHERE comment_closure.ancestor_id = ?1 AND comment_closure.ancestor_id != ?1
                ORDER BY comment_closure.depth
            """, nativeQuery = true)
    List<Comment> findAllDescendantsById(Long ancestorId);
}
