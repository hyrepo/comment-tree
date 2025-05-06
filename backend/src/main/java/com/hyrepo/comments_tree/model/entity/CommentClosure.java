package com.hyrepo.comments_tree.model.entity;

import jakarta.persistence.*;

/**
 * This class represents a closure table entry for a comment hierarchy.
 * It is used to store the ancestor-descendant relationships between comments
 * in a tree structure. The closure table pattern is applied here to efficiently
 * handle nested comments and comments' replies in a hierarchical manner.
 *
 * - ancestorId: The ID of the ancestor comment.
 * - descendantId: The ID of the descendant comment.
 * - depth: The depth of the descendant comment relative to the ancestor.
 *
 * Indexes are created for faster lookups by ancestor, descendant, and depth.
 *
 * The table schema is designed to optimize querying relationships between comments
 * without requiring recursive queries, thus improving performance for large datasets.
 */
@Entity
@Table(name = "comment_closure",
        indexes = {
                @Index(name = "idx_ancestor", columnList = "ancestor_id"),
                @Index(name = "idx_descendant", columnList = "descendant_id"),
        })
public class CommentClosure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ancestorId;

    @Column(nullable = false)
    private Long descendantId;

    // The depth of the descendant relative to the ancestor
    @Column(nullable = false)
    private int depth;

    public CommentClosure() {
    }

    public CommentClosure(Long id, Long ancestorId, Long descendantId, int depth) {
        this.id = id;
        this.ancestorId = ancestorId;
        this.descendantId = descendantId;
        this.depth = depth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAncestorId() {
        return ancestorId;
    }

    public void setAncestorId(Long ancestorId) {
        this.ancestorId = ancestorId;
    }

    public Long getDescendantId() {
        return descendantId;
    }

    public void setDescendantId(Long descendantId) {
        this.descendantId = descendantId;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
