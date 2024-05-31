package dev.mmaksymko.blogpost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private LocalDateTime postedAt;

    public PostResponse(PostResponse postResponse) {
        this.id = postResponse.id;
        this.title = postResponse.title;
        this.content = postResponse.content;
        this.authorId = postResponse.authorId;
        this.postedAt = postResponse.postedAt;
    }
}