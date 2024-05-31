package dev.mmaksymko.reactions.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private Boolean isDeleted;
    private Boolean isModified;
    private LocalDateTime commentedAt;
}