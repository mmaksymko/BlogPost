package dev.mmaksymko.blogpost.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record PostRequest(
    @NonNull String title,
    @NonNull String content,
    @NonNull Long authorId
) {}
