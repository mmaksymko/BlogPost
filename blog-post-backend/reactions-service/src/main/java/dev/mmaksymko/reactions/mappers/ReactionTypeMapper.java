package dev.mmaksymko.reactions.mappers;

import dev.mmaksymko.reactions.dto.ReactionTypeRequest;
import dev.mmaksymko.reactions.dto.ReactionTypeResponse;
import dev.mmaksymko.reactions.models.ReactionType;
import org.springframework.stereotype.Service;

@Service
public class ReactionTypeMapper {
    public ReactionType toEntity(ReactionTypeRequest request) {
        return toEntity(null, request);
    }

    public ReactionType toEntity(Long id, ReactionTypeRequest request) {
        return ReactionType
                .builder()
                .id(id)
                .name(request.name())
                .build();
    }

    public ReactionType toEntity(ReactionTypeResponse response) {
        return ReactionType
                .builder()
                .id(response.id())
                .name(response.name())
                .build();
    }

    public ReactionTypeResponse toResponse(ReactionType reaction) {
        return ReactionTypeResponse
                .builder()
                .id(reaction.getId())
                .name(reaction.getName())
                .build();
    }
}
