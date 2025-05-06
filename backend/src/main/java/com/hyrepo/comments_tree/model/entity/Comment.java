package com.hyrepo.comments_tree.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Timestamp in UTC+0 timezone
    @Column(nullable = false)
    private Long createdAt;

    public Comment() {
    }

    public Comment(Long id, String username, String content, Long createdAt) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Comment(String username, String content, Long createdAt) {
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
