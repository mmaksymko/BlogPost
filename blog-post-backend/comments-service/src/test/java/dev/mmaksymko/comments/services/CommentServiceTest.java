package dev.mmaksymko.comments.services;

import dev.mmaksymko.comments.clients.PostClient;
import dev.mmaksymko.comments.configs.exceptions.ResourceGoneException;
import dev.mmaksymko.comments.dto.BaseCommentResponse;
import dev.mmaksymko.comments.dto.CommentRequest;
import dev.mmaksymko.comments.dto.CommentResponse;
import dev.mmaksymko.comments.dto.CommentUpdateRequest;
import dev.mmaksymko.comments.mappers.CommentMapper;
import dev.mmaksymko.comments.models.Comment;
import dev.mmaksymko.comments.models.Post;
import dev.mmaksymko.comments.repositories.jpa.CommentRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentServiceTest {
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private CommentMapper commentMapper;
    @MockBean
    private PostClient postClient;

    private CommentService commentService;
    private Comment comment;
    private BaseCommentResponse baseCommentResponse;
    private CommentResponse commentResponse;
    private CommentRequest commentRequest;
    private Post post;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, commentMapper, postClient, null);

        comment = new Comment();
        baseCommentResponse = new BaseCommentResponse();
        commentResponse = new CommentResponse(baseCommentResponse);
        commentRequest = new CommentRequest(1L, null, 1L, "");
        post = new Post(commentRequest.postId(), "title", "content", 1L, LocalDateTime.now());
    }

    @Nested
    class GetCommentsTests {
        @Test
        @DisplayName("Returns empty list when no comments")
        void testGetCommentsNoComments() {
            final int EXPECTED_LENGTH = 0;

            when(commentRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

            Page<CommentResponse> comments = commentService.getComments(null, PageRequest.of(0, 10));

            assertEquals(EXPECTED_LENGTH, comments.getContent().size());

            verify(commentRepository, times(1)).findAll(any(PageRequest.class));
        }

        @Test
        @DisplayName("Returns list of comments when comments exist")
        void testGetCommentsWithComments() {
            final int EXPECTED_LENGTH = 1;

            when(commentRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.singletonList(comment)));
            when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

            Page<CommentResponse> comments = commentService.getComments(null, PageRequest.of(0, 10));

            assertEquals(EXPECTED_LENGTH, comments.getContent().size());
            assertEquals(commentResponse, comments.getContent().get(0));

            verify(commentRepository, times(1)).findAll(any(PageRequest.class));
            verify(commentMapper, times(1)).toResponse(any(Comment.class));
        }

        @Test
        @DisplayName("Returns list of comments for a specific post")
        void testGetCommentsWithPostId() {
            when(commentRepository.findAllByPostIdAndParentCommentIsNull(any(Pageable.class), anyLong())).thenReturn(new PageImpl<>(Collections.singletonList(comment)));
            when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

            Page<CommentResponse> comments = commentService.getComments(1L, PageRequest.of(0, 10));

            assertEquals(1, comments.getContent().size());
            assertEquals(commentResponse, comments.getContent().get(0));

            verify(commentRepository, times(1)).findAllByPostIdAndParentCommentIsNull(any(Pageable.class), anyLong());
            verify(commentMapper, times(1)).toResponse(any(Comment.class));
        }
    }

    @Nested
    class GetCommentTests {
        @ParameterizedTest
        @DisplayName("Returns the comment specified by id")
        @ValueSource(longs = {1L, Long.MAX_VALUE, Long.MIN_VALUE, 0})
        public void testGetCommentWhenCorrectId(Long id) {
            when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
            when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

            CommentResponse result = commentService.getComment(id);

            assertEquals(commentResponse, result);

            verify(commentRepository, times(1)).findById(id);
            verify(commentMapper, times(1)).toResponse(comment);
        }

        @Test
        @DisplayName("Throws NoSuchElementException when id is inexistent")
        public void testGetCommentWhenInexistentIdThrowsNoSuchElement() {
            Long insertId = 1L;
            Long testId = 2L;

            when(commentRepository.findById(any(Long.class))).thenAnswer(invocation -> {
                Long argument = invocation.getArgument(0);
                if (argument.equals(insertId)) {
                    return Optional.of(comment);
                } else {
                    return Optional.empty();
                }
            });

            when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

            assertThrows(NoSuchElementException.class, () -> commentService.getComment(testId));

            verify(commentRepository).findById(testId);
        }
    }

    @Nested
    class AddCommentTests {
        @BeforeEach
        void setUp() {
            baseCommentResponse = new BaseCommentResponse(1L, 1L, 1L, "", false, false, null);
        }

        @Test
        @DisplayName("Adds a new comment with no parentId")
        void testAddComment() {
            CommentResponse commentResponse = new CommentResponse(baseCommentResponse);
            commentRequest = new CommentRequest(1L, null, 1L, "");

            when(postClient.getPost(any(Long.class))).thenReturn(post);
            when(commentMapper.toEntity(any(CommentRequest.class), isNull())).thenReturn(comment);
            when(commentRepository.findById(any(Long.class))).thenReturn(Optional.empty());
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

            commentService.addComment(commentRequest);

            assertEquals(commentResponse.getPostId(), commentRequest.postId());
            assertEquals(commentResponse.getUserId(), commentRequest.userId());
            assertEquals(commentResponse.getContent(), commentRequest.content());

            verify(commentMapper, times(1)).toEntity(any(CommentRequest.class), isNull());
            verify(commentRepository, times(1)).save(any(Comment.class));
            verify(commentRepository, times(0)).findById(any(Long.class));
            verify(commentMapper, times(1)).toResponse(any(Comment.class));
            verify(postClient, times(1)).getPost(any(Long.class));
        }

        @ParameterizedTest
        @DisplayName("Adds a new comment with different parentIds")
        @ValueSource(longs = {1L, Long.MAX_VALUE, Long.MIN_VALUE, 0})
        void testAddCommentWithDifferentParentIds(Long parentId) {
            CommentResponse commentResponse = new CommentResponse(baseCommentResponse);
            commentRequest = new CommentRequest(1L, parentId, 1L, "");

            when(commentMapper.toEntity(any(CommentRequest.class), any(Comment.class))).thenReturn(comment);
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(commentRepository.findById(any(Long.class))).thenReturn(Optional.of(comment));
            when(commentMapper.toResponse(comment)).thenReturn(commentResponse);
            when(postClient.getPost(any(Long.class))).thenReturn(post);

            commentService.addComment(commentRequest);

            assertEquals(commentResponse.getPostId(), commentRequest.postId());
            assertEquals(commentResponse.getUserId(), commentRequest.userId());
            assertEquals(commentResponse.getContent(), commentRequest.content());

            verify(commentMapper, times(1)).toEntity(any(CommentRequest.class), any(Comment.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(commentMapper, times(1)).toResponse(any(Comment.class));
            verify(postClient, times(1)).getPost(any(Long.class));
        }
    }

    @Nested
    class UpdateCommentTests {
        private CommentUpdateRequest commentUpdateRequest;

        @BeforeEach
        void setUp() {
            commentUpdateRequest = new CommentUpdateRequest(null);
        }

        @Test
        @DisplayName("Updates an existing comment")
        void testUpdateComment() {
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

            CommentResponse result = commentService.updateComment(1L, commentUpdateRequest);

            assertEquals(commentResponse, result);

            verify(commentRepository, times(1)).findById(anyLong());
            verify(commentRepository, times(1)).save(any(Comment.class));
            verify(commentMapper, times(1)).toResponse(any(Comment.class));
        }

        @Test
        @DisplayName("Throws ResourceGoneException when comment is already deleted")
        void testUpdateCommentWithDeletedComment() {
            comment.setIsDeleted(true);

            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

            Assertions.assertThrows(ResourceGoneException.class, () -> commentService.updateComment(1L, commentUpdateRequest));
            verify(commentRepository, times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("Throws NoSuchElementException when comment does not exist")
        void testUpdateInexistentComment() {
            when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> commentService.updateComment(1L, commentUpdateRequest));

            verify(commentRepository, times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("Sets isModified to true when comment is updated")
        void testUpdateSetsIsModifiedToTrue() {
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

            commentService.updateComment(1L, commentUpdateRequest);

            assertTrue(comment.getIsModified());

            verify(commentRepository, times(1)).findById(anyLong());
            verify(commentRepository, times(1)).save(any(Comment.class));
        }
    }

    @Nested
    class DeleteCommentTests {
        @Test
        @DisplayName("Deletes an existing comment")
        void testDeleteComment() {
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

            commentService.deleteComment(1L);

            assertEquals(true, comment.getIsDeleted());

            verify(commentRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Throws ResourceGoneException when comment is already deleted")
        void testDeleteCommentWithDeletedComment() {
            comment.setIsDeleted(true);

            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

            Assertions.assertThrows(ResourceGoneException.class, () -> commentService.deleteComment(1L));

            verify(commentRepository, times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("Throws NoSuchElementException when comment does not exist")
        void testDeleteInexistentComment() {
            when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> commentService.deleteComment(1L));

            verify(commentRepository, times(1)).findById(anyLong());
        }
    }
}