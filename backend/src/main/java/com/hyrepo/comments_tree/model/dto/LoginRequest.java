package com.hyrepo.comments_tree.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record LoginRequest(@Schema(description = "Username or Email") String principal, String password) {
}
