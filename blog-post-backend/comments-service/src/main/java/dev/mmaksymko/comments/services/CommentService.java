package dev.mmaksymko.comments.services;

import dev.mmaksymko.comments.configs.ResourceGoneException;
import dev.mmaksymko.comments.dto.CommentRequest;
import dev.mmaksymko.comments.dto.CommentResponse;
import dev.mmaksymko.comments.dto.CommentUpdateRequest;
import dev.mmaksymko.comments.mappers.CommentMapper;
import dev.mmaksymko.comments.models.Comment;
import dev.mmaksymko.comments.repositories.CommentRepository;
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

    public Page<CommentResponse> getComments(Pageable pageable) {
        commentRepository.findAll(pageable).forEach(x -> System.out.println(x.getParentComment() == null ? null : x.getParentComment().getCommentId()));
        return commentRepository.findAll(pageable).map(commentMapper::toResponse);
    }

    public CommentResponse getComment(Long id) {
        return commentRepository.findById(id).map(commentMapper::toResponse).orElseThrow();
    }

    @Transactional
    @Modifying
    public CommentResponse addComment(CommentRequest request) {
        Comment parentComment = request.parentCommentId() != null
                ? commentRepository.findById(request.parentCommentId()).orElseThrow()
                : null;

        Comment comment = commentMapper.toEntity(request, parentComment);

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toResponse(savedComment);
    }

    @Transactional
    @Modifying
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
