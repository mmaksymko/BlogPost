package dev.mmaksymko.reactions.models;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Comment(
    Long commentId,
    Long postId,
    Long userId,
    String content,
    Boolean isDeleted,
    Boolean isModified,
    LocalDateTime commentedAt
) {}
