package com.hyrepo.comments_tree.repository;

import com.hyrepo.comments_tree.model.entity.CommentClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentClosureRepository extends JpaRepository<CommentClosure, Long> {
    @Modifying
    @Query(value = "INSERT INTO comment_closure (ancestor_id, descendant_id, depth) VALUES (?1, ?1, 0)", nativeQuery = true)
    void insertSelfRelation(Long commentId);

    @Modifying
    @Query(value = """
                INSERT INTO comment_closure (ancestor_id, descendant_id, depth)
                SELECT ancestor_id, ?2, depth + 1 FROM comment_closure WHERE descendant_id = ?1
            """, nativeQuery = true)
    void insertChildRelation(Long parentId, Long commentId);

    List<CommentClosure> findALlByDepth(int depth);
}
