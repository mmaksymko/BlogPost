package dev.mmaksymko.comments.services;

import dev.mmaksymko.comments.clients.PostClient;
import dev.mmaksymko.comments.configs.exceptions.ForbiddenException;
import dev.mmaksymko.comments.configs.exceptions.ResourceGoneException;
import dev.mmaksymko.comments.configs.security.Claims;
import dev.mmaksymko.comments.dto.BaseCommentResponse;
import dev.mmaksymko.comments.dto.CommentRequest;
import dev.mmaksymko.comments.dto.CommentResponse;
import dev.mmaksymko.comments.dto.CommentUpdateRequest;
import dev.mmaksymko.comments.mappers.CommentMapper;
import dev.mmaksymko.comments.models.Comment;
import dev.mmaksymko.comments.models.Post;
import dev.mmaksymko.comments.repositories.jpa.CommentRepository;
import dev.mmaksymko.comments.services.kafka.CommentProducer;
import dev.mmaksymko.comments.services.redis.RedisPostService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostClient postClient;
    private final CommentProducer commentProducer;
    private final RedisPostService redisPostService;
    private final Claims claims;

    public Page<CommentResponse> getComments(Long postId, Pageable pageable) {
        Page<Comment> comments;

        if (postId != null) {
            comments = commentRepository.findAllByPostIdAndParentCommentIsNull(pageable, postId);
        } else {
            comments = commentRepository.findAll(pageable);
        }

        return comments.map(commentMapper::toResponse);
    }

    public CommentResponse getComment(Long id) {
        return commentRepository.findById(id).map(commentMapper::toResponse).orElseThrow();
    }

    public BaseCommentResponse getCommentByItself(Long id) {
        return commentRepository.findById(id).map(commentMapper::toBaseCommentResponse).orElseThrow();
    }

    @Transactional
    @Modifying
    @CircuitBreaker(name = "circuit-breaker-comment")
    @Retry(name = "retry-comment")
    @RateLimiter(name = "rate-limit-comment")
    public CommentResponse addComment(CommentRequest request) {
        Post post = getPost(request.postId());

        Comment parentComment = request.parentCommentId() != null
                ? commentRepository.findById(request.parentCommentId()).orElseThrow()
                : null;

        Comment comment = commentMapper.toEntity(request, parentComment, getUserId());

        Comment savedComment = commentRepository.save(comment);

        CommentResponse commentResponse = commentMapper.toResponse(savedComment);
        commentProducer.sendCreatedEvent(commentResponse);

        return commentResponse;
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-comment")
    @RateLimiter(name = "rate-limit-comment")
    public CommentResponse updateComment(Long id, CommentUpdateRequest request) {
        Comment retrievedComment = commentRepository.findById(id).orElseThrow();

        if (retrievedComment.getIsDeleted()) {
            throw new ResourceGoneException("Comment is deleted");
        } else if (!isUserAllowedToModify(retrievedComment.getUserId())) {
            throw new ResourceGoneException("User is not allowed to modify this comment");
        }

        retrievedComment.setContent(request.content());
        retrievedComment.setIsModified(true);

        Comment savedComment = commentRepository.save(retrievedComment);

        CommentResponse commentResponse = commentMapper.toResponse(savedComment);
        commentProducer.sendUpdatedEvent(commentResponse);

        return commentResponse;
    }

    @Transactional
    @Modifying
    public void deleteComment(Long id) {
        Comment retrievedComment = commentRepository.findById(id).orElseThrow();

        System.out.println(retrievedComment.getUserId());
        System.out.println(claims.getClaim("id"));

        if (retrievedComment.getIsDeleted()) {
            throw new ResourceGoneException("Comment is already deleted");
        } else if (!isUserAllowedToModify(retrievedComment.getUserId())) {
            throw new ForbiddenException("User is not allowed to modify this comment");
        }

        retrievedComment.setIsDeleted(true);

        commentRepository.save(retrievedComment);

        CommentResponse commentResponse = commentMapper.toResponse(retrievedComment);

        commentProducer.sendDeletedEvent(commentResponse);
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
        return Optional.ofNullable(claims.getClaim("role"))
                .map(Object::toString)
                .map(role -> role.equals("ADMIN") || role.equals("SUPER_ADMIN") || userId.equals(getUserId()))
                .orElse(false);
    }

    private Long getUserId() {
        return Optional.ofNullable(claims.getClaim("id"))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(null);
    }
}
