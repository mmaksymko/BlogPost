package dev.mmaksymko.reactions.services;

import dev.mmaksymko.reactions.clients.CommentClient;
import dev.mmaksymko.reactions.configs.security.Claims;
import dev.mmaksymko.reactions.dto.CommentReactionRequest;
import dev.mmaksymko.reactions.dto.CommentReactionResponse;
import dev.mmaksymko.reactions.dto.ReactionCount;
import dev.mmaksymko.reactions.mappers.CommentReactionMapper;
import dev.mmaksymko.reactions.models.Comment;
import dev.mmaksymko.reactions.models.CommentReaction;
import dev.mmaksymko.reactions.models.ReactionType;
import dev.mmaksymko.reactions.repositories.jpa.CommentReactionRepository;
import dev.mmaksymko.reactions.services.redis.RedisCommentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    private final RedisCommentService redisCommentService;
    private final Claims claims;

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

    public CommentReactionResponse getCommentReaction(Long commentId) {
        var reactionId = CommentReaction
                .CommentReactionId
                .builder()
                .commentId(commentId)
                .userId(getUserId())
                .build();

        CommentReaction reaction = commentReactionRepository
                .findById(reactionId)
                .orElse(CommentReaction
                    .builder()
                    .reactionType(null)
                    .id(reactionId)
                    .build()
                );

        return commentReactionMapper.toResponse(reaction);
    }

    @Transactional
    @Modifying
    @CircuitBreaker(name = "circuit-breaker-reaction")
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public CommentReactionResponse addCommentReaction(CommentReactionRequest request) {
        Comment comment = commentClient.getComment(request.commentId());

        CommentReaction reaction = commentReactionMapper.toEntity(request, getUserId());

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
                .userId(getUserId())
                .build();

        commentReactionRepository.findById(reactionId).orElseThrow();

        CommentReaction reaction = commentReactionMapper.toEntity(request, reactionId.getUserId());

        CommentReaction savedReaction = commentReactionRepository.save(reaction);

        return commentReactionMapper.toResponse(savedReaction);
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public void deleteCommentReaction(Long commentId) {
        Long userId = getUserId();

        var reactionId = CommentReaction
                .CommentReactionId
                .builder()
                .commentId(commentId)
                .userId(userId)
                .build();

        commentReactionRepository.deleteById(reactionId);
    }

    private Comment getComment(Long id) {
        Comment comment = redisCommentService.getComment(id);
        if (comment == null) {
            comment = commentClient.getComment(id);
            redisCommentService.saveComment(comment);
        }
        return comment;
    }

    private boolean isUserAllowedToModify(Long userId) {
        return claims.getClaim("role").equals("ADMIN")
                || claims.getClaim("role").equals("SUPER_ADMIN")
                || userId.toString().equals(claims.getClaim("id"));
    }

    private Long getUserId() {
        return Long.valueOf(claims.getClaim("id").toString());
    }
}
