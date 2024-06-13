package dev.mmaksymko.reactions.services;

import dev.mmaksymko.reactions.clients.PostClient;
import dev.mmaksymko.reactions.configs.security.Claims;
import dev.mmaksymko.reactions.dto.PostReactionRequest;
import dev.mmaksymko.reactions.dto.PostReactionResponse;
import dev.mmaksymko.reactions.dto.ReactionCount;
import dev.mmaksymko.reactions.mappers.PostReactionMapper;
import dev.mmaksymko.reactions.models.Post;
import dev.mmaksymko.reactions.models.PostReaction;
import dev.mmaksymko.reactions.models.ReactionType;
import dev.mmaksymko.reactions.repositories.jpa.PostReactionRepository;
import dev.mmaksymko.reactions.services.redis.RedisPostService;
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
public class PostReactionService {
    private final PostReactionRepository postReactionRepository;
    private final PostReactionMapper postReactionMapper;
    private final PostClient postClient;
    private final ReactionTypeService reactionTypeService;
    private final RedisPostService redisPostService;
    private final Claims claims;

    public Page<PostReactionResponse> getPostReactions(Long commentId, Pageable pageable) {
        return postReactionRepository.findAllByIdPostId(commentId, pageable).map(postReactionMapper::toResponse);
    }

    public Map<String, Long> getPostReactionsCount(Long commentId) {
         Map<String, Long> countResult = postReactionRepository
                 .countReactionsByType(commentId)
                 .collect(Collectors.toMap(ReactionCount::reaction, ReactionCount::count));

        Map<String, ReactionType> allReactionTypes = reactionTypeService.getAllReactionTypes();

        return allReactionTypes.keySet().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        key -> countResult.getOrDefault(key, 0L)
                ));
    }

    public PostReactionResponse getPostReaction(Long commentId) {
        var reactionId = PostReaction
                .PostReactionId
                .builder()
                .postId(commentId)
                .userId(getUserId())
                .build();

        PostReaction reaction = postReactionRepository
                .findById(reactionId)
                .orElse(PostReaction
                        .builder()
                        .reactionType(null)
                        .id(reactionId)
                        .build()
                );

        return postReactionMapper.toResponse(reaction);
    }

    @Transactional
    @Modifying
    @CircuitBreaker(name = "circuit-breaker-reaction")
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public PostReactionResponse addPostReaction(PostReactionRequest request) {
        Post post = getPost(request.postId());

        PostReaction reaction = postReactionMapper.toEntity(request, getUserId());

        PostReaction savedReaction = postReactionRepository.save(reaction);

        return postReactionMapper.toResponse(savedReaction);
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public PostReactionResponse updatePostReaction(PostReactionRequest request) {
        var reactionId = PostReaction
                .PostReactionId
                .builder()
                .postId(request.postId())
                .userId(getUserId())
                .build();

        postReactionRepository.findById(reactionId).orElseThrow();

        PostReaction reaction = postReactionMapper.toEntity(request, reactionId.getUserId());

        PostReaction savedReaction = postReactionRepository.save(reaction);

        return postReactionMapper.toResponse(savedReaction);
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public void deletePostReaction(Long commentId) {
        Long userId = getUserId();

        var reactionId = PostReaction
                .PostReactionId
                .builder()
                .postId(commentId)
                .userId(userId)
                .build();

        postReactionRepository.deleteById(reactionId);
    }

    private Post getPost(Long id) {
        Post post = redisPostService.getPost(id);
        if (post == null) {
            post = postClient.getPost(id);
            redisPostService.savePost(post);
        }
        return post;
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
