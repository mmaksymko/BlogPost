package dev.mmaksymko.blogpost.services;

import dev.mmaksymko.blogpost.dto.PostRequest;
import dev.mmaksymko.blogpost.dto.PostResponse;
import dev.mmaksymko.blogpost.dto.PostUpdateRequest;
import dev.mmaksymko.blogpost.mappers.PostMapper;
import dev.mmaksymko.blogpost.models.Post;
import dev.mmaksymko.blogpost.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @DisplayName("GetPosts: returns a page of posts")
    public void testGetPosts() {
        Post post = Post.builder().title("Test Title").content("Test Content").build();
        PostResponse postResponse = new PostResponse(1L, "Test Title", "Test Content", 1L, null);
        Pageable pageable = Pageable.unpaged();

        when(postRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(post)));
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        Page<PostResponse> result = postService.getPosts(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(postResponse, result.getContent().get(0));
        verify(postRepository).findAll(pageable);
        verify(postMapper).toResponse(post);
    }

    @Test
    @DisplayName("GetPosts: returns empty page when none inserted")
    public void testGetPostsWhenEmpty() {
        Pageable pageable = Pageable.unpaged();

        when(postRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<PostResponse> result = postService.getPosts(pageable);

        assertEquals(0, result.getContent().size());
        verify(postRepository).findAll(pageable);
    }

    @ParameterizedTest
    @DisplayName("GetPost: returns the post specified by id")
    @ValueSource(longs = {1L, 2L, 3L})
    public void testGetPostWhenCorrectId(Long id) {
        Post post = Post.builder().title("Test Title").content("Test Content").build();
        PostResponse postResponse = new PostResponse(id, "Test Title", "Test Content", 1L, null);

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.getPost(id);

        assertEquals(postResponse, result);
        verify(postRepository).findById(id);
        verify(postMapper).toResponse(post);
    }

    @Test
    @DisplayName("GetPost: throws NoSuchElementException when id is inexistent")
    public void testGetPostWhenInexistentIdThrowsNoSuchElement() {
        Long insertId = 1L;
        Long testId = 2L;
        Post post = Post.builder().title("Test Title").content("Test Content").build();
        PostResponse postResponse = new PostResponse(insertId, "Test Title", "Test Content", 1L, null);

        when(postRepository.findById(any(Long.class))).thenAnswer(invocation -> {
            Long argument = invocation.getArgument(0);
            if (argument.equals(insertId)) {
                return Optional.of(post);
            } else {
                return Optional.empty();
            }
        });

        when(postMapper.toResponse(post)).thenReturn(postResponse);

        assertThrows(NoSuchElementException.class, () -> postService.getPost(2L));
        verify(postRepository).findById(testId);
    }


    @Test
    @DisplayName("AddPost: added post is returned")
    public void testAddPost() {
        PostRequest postRequest = new PostRequest("Test Title", "Test Content", 1L);
        Post post = Post.builder().title("Test Title").content("Test Content").build();
        PostResponse postResponse = new PostResponse(1L, "Test Title", "Test Content", 1L, null);

        when(postMapper.toEntity(any(PostRequest.class))).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.addPost(postRequest);

        assertEquals(postResponse, result);

        verify(postMapper).toEntity(postRequest);
        verify(postRepository).save(post);
        verify(postMapper).toResponse(post);
    }

    @Test
    @DisplayName("UpdatePost: updated post is returned")
    public void testUpdatePost() {
        Long id = 1L;
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("Updated Title", "Updated Content");
        Post post = Post.builder().title("Test Title").content("Test Content").build();
        PostResponse postResponse = new PostResponse(id, "Updated Title", "Updated Content", 1L, null);

        when(postRepository.findById(any(Long.class))).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PostResponse result = postService.updatePost(id, postUpdateRequest);

        assertEquals(postResponse, result);
        verify(postRepository).findById(id);
        verify(postRepository).save(post);
        verify(postMapper).toResponse(post);
    }

    @Test
    @DisplayName("UpdatePost: throws exception when post does not exist")
    public void testUpdatePostWhenInexistentIdThrowsNoSuchElement() {
        Long id = 1L;
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("Updated Title", "Updated Content");

        when(postRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> postService.updatePost(id, postUpdateRequest));

        verify(postRepository).findById(id);
    }
    @Test
    @DisplayName("DeletePost: post is deleted")
    public void testDeletePost() {
        Long id = 1L;

        doNothing().when(postRepository).deleteById(id);

        postService.deletePost(id);

        verify(postRepository).deleteById(id);
    }
}