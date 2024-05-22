package dev.mmaksymko.blogpost.dto;

public record PostUpdateRequest(
        String title,
        String content
) {}
