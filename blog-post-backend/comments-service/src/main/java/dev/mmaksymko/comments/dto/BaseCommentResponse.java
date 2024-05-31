package dev.mmaksymko.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseCommentResponse {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private Boolean isDeleted;
    private Boolean isModified;
    private LocalDateTime commentedAt;

    public BaseCommentResponse(BaseCommentResponse commentResponse) {
        this.commentId = commentResponse.getCommentId();
        this.postId = commentResponse.getPostId();
        this.userId = commentResponse.getUserId();
        this.content = commentResponse.getContent();
        this.isDeleted = commentResponse.getIsDeleted();
        this.isModified = commentResponse.getIsModified();
        this.commentedAt = commentResponse.getCommentedAt();
    }
}
