package dev.mmaksymko.comments.models;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Post(
        Long id,
        String title,
        String content,
        Long authorId,
        LocalDateTime postedAt
) {}
