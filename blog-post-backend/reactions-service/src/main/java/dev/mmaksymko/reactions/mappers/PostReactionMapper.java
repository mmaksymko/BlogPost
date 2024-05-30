package dev.mmaksymko.reactions.mappers;

import dev.mmaksymko.reactions.dto.CommentReactionRequest;
import dev.mmaksymko.reactions.dto.CommentReactionResponse;
import dev.mmaksymko.reactions.dto.PostReactionRequest;
import dev.mmaksymko.reactions.dto.PostReactionResponse;
import dev.mmaksymko.reactions.models.CommentReaction;
import dev.mmaksymko.reactions.models.PostReaction;
import dev.mmaksymko.reactions.services.ReactionTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostReactionMapper {
    private final ReactionTypeService reactionTypeService;
    private final ReactionTypeMapper reactionTypeMapper;

    public PostReaction toEntity(PostReactionRequest request) {
        var id = PostReaction
                .PostReactionId
                .builder()
                .postId(request.postId())
                .userId(request.userId())
                .build();
        var reactionTypes = reactionTypeService.getAllReactionTypes();
        var reactionType = Optional.ofNullable(reactionTypes.get(request.reaction()))
                .orElseThrow(() -> new NoSuchElementException("Reaction type not found"));

        return PostReaction
                .builder()
                .id(id)
                .reactionType(reactionType)
                .build();
    }

    public PostReactionResponse toResponse(PostReaction reaction) {
        return PostReactionResponse
                .builder()
                .postId(reaction.getId().getPostId())
                .userId(reaction.getId().getUserId())
                .reaction(reactionTypeMapper.toResponse(reaction.getReactionType()))
                .build();
    }
}
