package dev.mmaksymko.comments.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Builder
public record CommentRequest(
    @NonNull Long postId,
    @Nullable Long parentCommentId,
    @NonNull Long userId,
    @NonNull String content
) {}
