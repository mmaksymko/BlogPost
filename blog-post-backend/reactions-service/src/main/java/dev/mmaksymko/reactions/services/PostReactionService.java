package dev.mmaksymko.reactions.services;

import dev.mmaksymko.reactions.clients.PostClient;
import dev.mmaksymko.reactions.dto.PostReactionRequest;
import dev.mmaksymko.reactions.dto.PostReactionResponse;
import dev.mmaksymko.reactions.mappers.PostReactionMapper;
import dev.mmaksymko.reactions.models.Post;
import dev.mmaksymko.reactions.models.PostReaction;
import dev.mmaksymko.reactions.repositories.PostReactionRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class PostReactionService {
    private final PostReactionRepository postReactionRepository;
    private final PostReactionMapper postReactionMapper;
    private final PostClient postClient;

    public Page<PostReactionResponse> getPostReactions(Long commentId, Pageable pageable) {
        return postReactionRepository.findAllByIdPostId(commentId, pageable).map(postReactionMapper::toResponse);
    }

    public Map<String, Long> getPostReactionsCount(Long commentId) {
        return postReactionRepository.countReactionsByType(commentId);
    }

    public PostReactionResponse getPostReaction(Long commentId, Long userId) {
        var reactionId = PostReaction
                .PostReactionId
                .builder()
                .postId(commentId)
                .userId(userId)
                .build();

        PostReaction reaction = postReactionRepository.findById(reactionId).orElseThrow();

        return postReactionMapper.toResponse(reaction);
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public PostReactionResponse addPostReaction(PostReactionRequest request) {
        Post post = postClient.getPost(request.postId());

        PostReaction reaction = postReactionMapper.toEntity(request);

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
                .userId(request.userId())
                .build();

        postReactionRepository.findById(reactionId).orElseThrow();

        PostReaction reaction = postReactionMapper.toEntity(request);

        PostReaction savedReaction = postReactionRepository.save(reaction);

        return postReactionMapper.toResponse(savedReaction);
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public void deletePostReaction(Long commentId, Long userId) {
        var reactionId = PostReaction
                .PostReactionId
                .builder()
                .postId(commentId)
                .userId(userId)
                .build();

        postReactionRepository.deleteById(reactionId);
    }
}
