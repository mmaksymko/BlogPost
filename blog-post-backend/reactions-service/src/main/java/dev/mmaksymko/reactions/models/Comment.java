package dev.mmaksymko.reactions.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Comment")
public class Comment {
    @Id
    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private Boolean isDeleted;
    private Boolean isModified;
    private LocalDateTime commentedAt;
}