package dev.mmaksymko.reactions.dto;

import org.springframework.lang.NonNull;

public record PostReactionRequest(
    @NonNull Long postId,
    @NonNull Long userId,
    @NonNull String reaction
) {}
