package com.hyrepo.comments_tree.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "login_records")
public class LoginRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private long userId;

    // In UTC+0 timezone
    @Column(nullable = false)
    private long timestamp;

    @Column(nullable = false)
    private String token;

    public LoginRecord() {
    }

    public LoginRecord(long id, long userId, long timestamp, String token) {
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
        this.token = token;
    }

    public LoginRecord(long userId, long timestamp, String token) {
        this(0L, userId, timestamp, token);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
