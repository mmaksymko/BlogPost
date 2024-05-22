package dev.mmaksymko.comments.mappers;

import dev.mmaksymko.comments.dto.*;
import dev.mmaksymko.comments.models.Comment;
import org.springframework.stereotype.Service;

@Service
public class CommentMapper {
    public BaseCommentResponse toBaseCommentResponse(Comment comment) {
        if (comment.getIsDeleted()) {
            return BaseCommentResponse
                    .builder()
                    .postId(comment.getPostId())
                    .commentId(comment.getCommentId())
                    .isDeleted(comment.getIsDeleted())
                    .build();
        }

        return BaseCommentResponse
                .builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .isDeleted(comment.getIsDeleted())
                .isModified(comment.getIsModified())
                .commentedAt(comment.getCommentedAt())
                .build();
    }

    public CommentResponse toResponse(Comment comment) {
        CommentResponse response = new CommentResponse(toBaseCommentResponse(comment));
        response.setParentComment(comment.getParentComment() != null ? toChildlessResponse(comment.getParentComment()) : null);
        response.setSubComments(comment.getSubComments().stream().map(this::toParentlessResponse).toList());
        return response;
    }

    public ChildlessCommentResponse toChildlessResponse(Comment comment) {
        ChildlessCommentResponse response = new ChildlessCommentResponse(toBaseCommentResponse(comment));
        response.setParentComment(comment.getParentComment() != null ? toChildlessResponse(comment.getParentComment()) : null);
        return response;
    }

    public ParentlessCommentResponse toParentlessResponse(Comment comment) {
        ParentlessCommentResponse response = new ParentlessCommentResponse(toBaseCommentResponse(comment));
        response.setSubComments(comment.getSubComments().stream().map(this::toParentlessResponse).toList());
        return response;
    }


    public Comment toEntity(CommentRequest commentRequest, Comment parentComment) {
        return Comment
                .builder()
                .postId(commentRequest.postId())
                .parentComment(parentComment)
                .userId(commentRequest.userId())
                .content(commentRequest.content())
                .build();
    }

}
