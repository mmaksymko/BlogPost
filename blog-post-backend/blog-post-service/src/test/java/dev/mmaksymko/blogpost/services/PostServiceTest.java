package dev.mmaksymko.blogpost.services;

import dev.mmaksymko.blogpost.dto.PostRequest;
import dev.mmaksymko.blogpost.dto.PostResponse;
import dev.mmaksymko.blogpost.dto.PostUpdateRequest;
import dev.mmaksymko.blogpost.mappers.PostMapper;
import dev.mmaksymko.blogpost.models.Post;
import dev.mmaksymko.blogpost.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private Post post;
    private PostResponse postResponse;
    private PostRequest postRequest;
    private PostUpdateRequest postUpdateRequest;
    private Pageable pageable;

    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);
        post = Post.builder().title("Test Title").content("Test Content").build();
        postResponse = new PostResponse(1L, "Test Title", "Test Content", 1L, null);
        postRequest = new PostRequest("Test Title", "Test Content", 1L);
        postUpdateRequest = new PostUpdateRequest("Updated Title", "Updated Content");
        pageable = Pageable.unpaged();
    }


    @Nested
    class GetPostsTests {
        @Test
        @DisplayName("returns a page of posts")
        public void whenPostsExist() {
            when(postRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(post)));
            when(postMapper.toResponse(post)).thenReturn(postResponse);

            Page<PostResponse> result = postService.getPosts(pageable);

            assertEquals(1, result.getContent().size());
            assertEquals(postResponse, result.getContent().get(0));
            verify(postRepository).findAll(pageable);
            verify(postMapper).toResponse(post);
        }

        @Test
        @DisplayName("returns empty page when none inserted")
        public void whenNoPostsExist() {
            when(postRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

            Page<PostResponse> result = postService.getPosts(pageable);

            assertEquals(0, result.getContent().size());
            verify(postRepository).findAll(pageable);
        }
    }

    @Nested
    class GetPostTests {
        @ParameterizedTest
        @DisplayName("returns the post specified by id")
        @ValueSource(longs = {1L, 2L, 3L})
        public void whenIdExists(Long id) {
            when(postRepository.findById(id)).thenReturn(Optional.of(post));
            when(postMapper.toResponse(post)).thenReturn(postResponse);

            PostResponse result = postService.getPost(id);

            assertEquals(postResponse, result);
            verify(postRepository).findById(id);
            verify(postMapper).toResponse(post);
        }

        @Test
        @DisplayName("throws NoSuchElementException when id is inexistent")
        public void whenIdDoesNotExist() {
            Long testId = 2L;

            when(postRepository.findById(any(Long.class))).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> postService.getPost(testId));
            verify(postRepository).findById(testId);
        }
    }

    @Nested
    class AddPostTests {
        @Test
        @DisplayName("added post is returned")
        public void whenPostIsValid() {
            when(postMapper.toEntity(any(PostRequest.class))).thenReturn(post);
            when(postRepository.save(any(Post.class))).thenReturn(post);
            when(postMapper.toResponse(post)).thenReturn(postResponse);

            PostResponse result = postService.addPost(postRequest);

            assertEquals(postResponse, result);
            verify(postMapper).toEntity(postRequest);
            verify(postRepository).save(post);
            verify(postMapper).toResponse(post);
        }
    }

    @Nested
    class UpdatePostTests {
        @Test
        @DisplayName("updated post is returned")
        public void whenPostExists() {
            Long id = 1L;

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
        @DisplayName("throws exception when post does not exist")
        public void whenPostDoesNotExist() {
            Long id = 1L;

            when(postRepository.findById(any(Long.class))).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> postService.updatePost(id, postUpdateRequest));
            verify(postRepository).findById(id);
        }
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