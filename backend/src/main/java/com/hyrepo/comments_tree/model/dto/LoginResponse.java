package com.hyrepo.comments_tree.model.dto;

import com.hyrepo.comments_tree.model.UserInfo;

public record LoginResponse(String token, UserInfo user) {
}
