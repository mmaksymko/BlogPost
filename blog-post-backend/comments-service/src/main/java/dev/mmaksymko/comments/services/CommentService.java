package dev.mmaksymko.comments.services;

import dev.mmaksymko.comments.clients.PostClient;
import dev.mmaksymko.comments.configs.exceptions.ResourceGoneException;
import dev.mmaksymko.comments.dto.BaseCommentResponse;
import dev.mmaksymko.comments.dto.CommentRequest;
import dev.mmaksymko.comments.dto.CommentResponse;
import dev.mmaksymko.comments.dto.CommentUpdateRequest;
import dev.mmaksymko.comments.mappers.CommentMapper;
import dev.mmaksymko.comments.models.Comment;
import dev.mmaksymko.comments.models.Post;
import dev.mmaksymko.comments.repositories.CommentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostClient postClient;

    public Page<CommentResponse> getComments(Long postId, Pageable pageable) {
        Page<Comment> comments;

        if (postId != null) {
            comments = commentRepository.findAllByPostIdAndParentCommentIsNull(pageable, postId);
        } else {
            comments = commentRepository.findAll(pageable);
        }

        return comments.map(commentMapper::toResponse);
    }

    @Transactional
    @Modifying
    @CircuitBreaker(name = "circuit-breaker-comment")
    @Retry(name = "retry-comment")
    @RateLimiter(name = "rate-limit-comment")
    public CommentResponse addComment(CommentRequest request) {
        Post post = postClient.getPost(request.postId());

        Comment parentComment = request.parentCommentId() != null
                ? commentRepository.findById(request.parentCommentId()).orElseThrow()
                : null;

        Comment comment = commentMapper.toEntity(request, parentComment);

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toResponse(savedComment);
    }

    public CommentResponse getComment(Long id) {
        return commentRepository.findById(id).map(commentMapper::toResponse).orElseThrow();
    }

    public BaseCommentResponse getCommentByItself(Long id) {
        return commentRepository.findById(id).map(commentMapper::toBaseCommentResponse).orElseThrow();
    }

    @Transactional
    @Modifying
    @Retry(name = "retry-comment")
    @RateLimiter(name = "rate-limit-comment")
    public CommentResponse updateComment(Long id, CommentUpdateRequest request) {
        Comment retrievedComment = commentRepository.findById(id).orElseThrow();

        if (retrievedComment.getIsDeleted()) {
            throw new ResourceGoneException("Comment is deleted");
        }

        retrievedComment.setContent(request.content());
        retrievedComment.setIsModified(true);

        Comment savedComment = commentRepository.save(retrievedComment);

        return commentMapper.toResponse(savedComment);
    }

    @Transactional
    @Modifying
    public void deleteComment(Long id) {
        Comment retrievedComment = commentRepository.findById(id).orElseThrow();

        if (retrievedComment.getIsDeleted()) {
            throw new ResourceGoneException("Comment is deleted");
        }

        retrievedComment.setIsDeleted(true);
    }
}
