package dev.mmaksymko.reactions.services;

import dev.mmaksymko.reactions.clients.CommentClient;
import dev.mmaksymko.reactions.dto.CommentReactionRequest;
import dev.mmaksymko.reactions.dto.CommentReactionResponse;
import dev.mmaksymko.reactions.dto.ReactionCount;
import dev.mmaksymko.reactions.mappers.CommentReactionMapper;
import dev.mmaksymko.reactions.models.Comment;
import dev.mmaksymko.reactions.models.CommentReaction;
import dev.mmaksymko.reactions.models.ReactionType;
import dev.mmaksymko.reactions.repositories.CommentReactionRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentReactionService {
    private final CommentReactionRepository commentReactionRepository;
    private final CommentReactionMapper commentReactionMapper;
    private final CommentClient commentClient;
    private final ReactionTypeService reactionTypeService;

    public Page<CommentReactionResponse> getCommentReactions(Long commentId, Pageable pageable) {
        return commentReactionRepository.findAllByIdCommentId(commentId, pageable).map(commentReactionMapper::toResponse);
    }

    public Map<String, Long> getCommentReactionsCount(Long commentId) {
        Map<String, Long> countResult = commentReactionRepository
                .countReactionsByType(commentId)
                .collect(Collectors.toMap(ReactionCount::reaction, ReactionCount::count));

        Map<String, ReactionType> allReactionTypes = reactionTypeService.getAllReactionTypes();

        return allReactionTypes.keySet().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        key -> countResult.getOrDefault(key, 0L)
                ));
    }

    public CommentReactionResponse getCommentReaction(Long commentId, Long userId) {
        var reactionId = CommentReaction
                .CommentReactionId
                .builder()
                .commentId(commentId)
                .userId(userId)
                .build();

        CommentReaction reaction = commentReactionRepository.findById(reactionId).orElseThrow();

        return commentReactionMapper.toResponse(reaction);
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public CommentReactionResponse addCommentReaction(CommentReactionRequest request) {
        Comment comment = commentClient.getComment(request.commentId());

        CommentReaction reaction = commentReactionMapper.toEntity(request);

        CommentReaction savedReaction = commentReactionRepository.save(reaction);

        return commentReactionMapper.toResponse(savedReaction);
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public CommentReactionResponse updateCommentReaction(CommentReactionRequest request) {
        var reactionId = CommentReaction
                .CommentReactionId
                .builder()
                .commentId(request.commentId())
                .userId(request.userId())
                .build();

        commentReactionRepository.findById(reactionId).orElseThrow();

        CommentReaction reaction = commentReactionMapper.toEntity(request);

        CommentReaction savedReaction = commentReactionRepository.save(reaction);

        return commentReactionMapper.toResponse(savedReaction);
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public void deleteCommentReaction(Long commentId, Long userId) {
        var reactionId = CommentReaction
                .CommentReactionId
                .builder()
                .commentId(commentId)
                .userId(userId)
                .build();

        commentReactionRepository.deleteById(reactionId);
    }
}