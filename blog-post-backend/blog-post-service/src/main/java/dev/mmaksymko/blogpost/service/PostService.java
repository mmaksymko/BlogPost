package dev.mmaksymko.blogpost.service;

import dev.mmaksymko.blogpost.dto.PostRequest;
import dev.mmaksymko.blogpost.dto.PostResponse;
import dev.mmaksymko.blogpost.mappers.PostMapper;
import dev.mmaksymko.blogpost.models.Post;
import dev.mmaksymko.blogpost.repositories.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(postMapper::toResponse);
    }

    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow();

        return postMapper.toResponse(post);
    }

    public PostResponse addPost(PostRequest requestPost) {
        Post post = postMapper.toEntity(requestPost);

        Post savedPost = postRepository.save(post);

        return postMapper.toResponse(savedPost);
    }

    public PostResponse updatePost(Long id, PostRequest requestPost) {
        Post retrievedPost = postRepository.findById(id).orElseThrow();

        Post post = postMapper.toEntity(id, requestPost);

        Post savedPost = postRepository.save(post);

        return postMapper.toResponse(savedPost);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
