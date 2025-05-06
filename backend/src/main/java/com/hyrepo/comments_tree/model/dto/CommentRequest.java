package com.hyrepo.comments_tree.model.dto;

import com.hyrepo.comments_tree.util.Constants;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import static com.hyrepo.comments_tree.util.Constants.COMMENT_LENGTH_MAX;

public record CommentRequest(
        @NotEmpty
        String username,
        @Length(min = Constants.COMMENT_LENGTH_MIN, max = COMMENT_LENGTH_MAX, message = "Comment length is invalid.")
        String content,
        Long parentId) {
}
