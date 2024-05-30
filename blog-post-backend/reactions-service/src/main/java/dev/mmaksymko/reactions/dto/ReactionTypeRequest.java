package dev.mmaksymko.reactions.dto;

import org.springframework.lang.NonNull;

public record ReactionTypeRequest(
    @NonNull String name
) {}
