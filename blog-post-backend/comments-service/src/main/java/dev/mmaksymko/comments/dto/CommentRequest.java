package dev.mmaksymko.comments.dto;

import lombok.Builder;

@Builder
public record CommentRequest(
        Long postId,
        Long parentCommentId,
        Long userId,
        String content
) {}
