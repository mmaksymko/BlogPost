package dev.mmaksymko.comments.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Post")
public class Post {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private LocalDateTime postedAt;
}
