package dev.mmaksymko.reactions.dto;

import lombok.Builder;

@Builder
public record CommentReactionResponse(
    Long commentId,
    Long userId,
    ReactionTypeResponse reaction
) {}
