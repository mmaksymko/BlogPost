package dev.mmaksymko.blogpost.services;

import dev.mmaksymko.blogpost.dto.PostRequest;
import dev.mmaksymko.blogpost.dto.PostResponse;
import dev.mmaksymko.blogpost.dto.PostUpdateRequest;
import dev.mmaksymko.blogpost.mappers.PostMapper;
import dev.mmaksymko.blogpost.models.Post;
import dev.mmaksymko.blogpost.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @InjectMocks
    private PostService postService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPosts() {
        Post post = new Post();
        post.setTitle("Test Title");
        post.setContent("Test Content");

        PostResponse postResponse = new PostResponse(1L, "Test Title", "Test Content", 1L, null);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(post)));
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        Page<PostResponse> result = postService.getPosts(Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        assertEquals(postResponse, result.getContent().get(0));
    }

    @Test
    public void testGetPost() {
        Post post = new Post();
        post.setTitle("Test Title");
        post.setContent("Test Content");

        PostResponse postResponse = new PostResponse(1L, "Test Title", "Test Content", 1L, null);

        when(postRepository.findById(any(Long.class))).thenReturn(Optional.of(post));
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.getPost(1L);

        assertEquals(postResponse, result);
    }

    @Test
    public void testAddPost() {
        PostRequest postRequest = new PostRequest("Test Title", "Test Content", 1L);
        Post post = new Post();
        post.setTitle("Test Title");
        post.setContent("Test Content");

        PostResponse postResponse = new PostResponse(1L, "Test Title", "Test Content", 1L, null);

        when(postMapper.toEntity(any(PostRequest.class))).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.addPost(postRequest);

        assertEquals(postResponse, result);
    }

    @Test
    public void testUpdatePost() {
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("Updated Title", "Updated Content");
        Post post = new Post();
        post.setTitle("Test Title");
        post.setContent("Test Content");

        PostResponse postResponse = new PostResponse(1L, "Updated Title", "Updated Content", 1L, null);

        when(postRepository.findById(any(Long.class))).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.updatePost(1L, postUpdateRequest);

        assertEquals(postResponse, result);
    }
}