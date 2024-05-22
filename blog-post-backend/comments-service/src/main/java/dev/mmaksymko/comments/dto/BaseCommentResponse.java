package dev.mmaksymko.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
@AllArgsConstructor
public class BaseCommentResponse {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private Boolean isDeleted;
    private Boolean isModified;
    private LocalDateTime commentedAt;
}
