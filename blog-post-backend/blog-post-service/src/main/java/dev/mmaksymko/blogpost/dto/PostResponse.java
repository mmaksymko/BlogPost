package dev.mmaksymko.blogpost.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponse(
    Long id,
    String title,
    String content,
    Long authorId,
    LocalDateTime postedAt
) {}