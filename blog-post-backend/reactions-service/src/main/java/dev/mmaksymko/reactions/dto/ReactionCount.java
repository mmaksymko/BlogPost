package dev.mmaksymko.reactions.dto;

import org.springframework.lang.NonNull;

public record ReactionCount(
    @NonNull String reaction,
    @NonNull Long count
) {}
