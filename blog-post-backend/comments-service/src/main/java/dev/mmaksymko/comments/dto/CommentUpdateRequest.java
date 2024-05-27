package dev.mmaksymko.comments.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record CommentUpdateRequest(
    @NonNull String content
) {}
