package dev.mmaksymko.blogpost.dto;

import lombok.Builder;

@Builder
public record PostRequest(
    String title,
    String content,
    Long authorId
) {}
