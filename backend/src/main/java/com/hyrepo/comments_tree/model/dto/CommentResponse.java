package com.hyrepo.comments_tree.model.dto;

import com.hyrepo.comments_tree.model.entity.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentResponse {
    private long id;
    private String username;
    private String content;
    // Timestamp in UTC+0 timezone
    private long createdAt;
    private Long parentId;
    private List<CommentResponse> comments;

    public CommentResponse() {
    }

    public CommentResponse(long id, String username, String content, long createdAt, Long parentId, List<CommentResponse> comments) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
        this.parentId = parentId;
        this.comments = comments;
    }

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.username = comment.getUsername();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.parentId = null;
        this.comments = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<CommentResponse> comments) {
        this.comments = comments;
    }

    public void sortComments() {
        comments.sort((o1, o2) -> (int) (o2.createdAt - o1.createdAt));
        comments.stream().filter(it -> !it.comments.isEmpty()).forEach(CommentResponse::sortComments);
    }
}
