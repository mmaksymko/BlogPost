package dev.mmaksymko.reactions.mappers;

import dev.mmaksymko.reactions.dto.CommentReactionRequest;
import dev.mmaksymko.reactions.dto.CommentReactionResponse;
import dev.mmaksymko.reactions.models.CommentReaction;
import dev.mmaksymko.reactions.services.ReactionTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentReactionMapper {
    private final ReactionTypeService reactionTypeService;
    private final ReactionTypeMapper reactionTypeMapper;

    public CommentReaction toEntity(CommentReactionRequest request, Long userId) {
        var id = CommentReaction
                .CommentReactionId
                .builder()
                .commentId(request.commentId())
                .userId(userId)
                .build();
        var reactionTypes = reactionTypeService.getAllReactionTypes();
        var reactionType = Optional.ofNullable(reactionTypes.get(request.reaction()))
                .orElseThrow(() -> new NoSuchElementException("Reaction type not found"));

        return CommentReaction
                .builder()
                .id(id)
                .reactionType(reactionType)
                .build();
    }

    public CommentReactionResponse toResponse(CommentReaction reaction) {
        return CommentReactionResponse
                .builder()
                .commentId(reaction.getId().getCommentId())
                .userId(reaction.getId().getUserId())
                .reaction(reactionTypeMapper.toResponse(reaction.getReactionType()))
                .build();
    }
}
