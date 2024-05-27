package dev.mmaksymko.blogpost.dto;

import org.springframework.lang.NonNull;

public record PostUpdateRequest(
    @NonNull String title,
    @NonNull String content
) {}
