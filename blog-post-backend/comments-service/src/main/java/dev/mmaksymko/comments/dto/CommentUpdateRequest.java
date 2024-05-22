package dev.mmaksymko.comments.dto;

import lombok.Builder;

@Builder
public record CommentUpdateRequest(
    String content
) {}
