package dev.mmaksymko.reactions.dto;

import org.springframework.lang.NonNull;

public record CommentReactionRequest(
    @NonNull Long commentId,
    @NonNull String reaction
) {}
