package dev.mmaksymko.blogpost.mappers;

import dev.mmaksymko.blogpost.dto.PostRequest;
import dev.mmaksymko.blogpost.dto.PostResponse;
import dev.mmaksymko.blogpost.models.Post;
import org.springframework.stereotype.Service;

@Service
public class PostMapper {
    public PostResponse toResponse(Post post) {
        return PostResponse
                .builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .postedAt(post.getPostedAt())
                .build();
    }

    public Post toEntity(PostRequest postRequest) {
        return Post
                .builder()
                .title(postRequest.title())
                .content(postRequest.content())
                .authorId(postRequest.authorId())
                .build();
    }
}
