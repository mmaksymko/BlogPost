package dev.mmaksymko.reactions.dto;

import lombok.Builder;

@Builder
public record PostReactionResponse(
    Long postId,
    Long userId,
    ReactionTypeResponse reaction
) {}
