package dev.mmaksymko.reactions.dto;

import lombok.Builder;

@Builder
public record ReactionTypeResponse (
    Long id,
    String name
){}
