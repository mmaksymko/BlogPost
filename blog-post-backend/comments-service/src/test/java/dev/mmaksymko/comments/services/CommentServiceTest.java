package dev.mmaksymko.comments.services;

import dev.mmaksymko.comments.dto.CommentRequest;
import dev.mmaksymko.comments.dto.CommentResponse;
import dev.mmaksymko.comments.dto.CommentUpdateRequest;
import dev.mmaksymko.comments.mappers.CommentMapper;
import dev.mmaksymko.comments.models.Comment;
import dev.mmaksymko.comments.repositories.CommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentServiceTest {

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private CommentMapper commentMapper;

    @Test
    void testGetComments() {
        CommentService commentService = new CommentService(commentRepository, commentMapper);
        Comment comment = new Comment();
        CommentResponse commentResponse = new CommentResponse(null, null, null, null, null, null, null, null);
        when(commentRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.singletonList(comment)));
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

        Page<CommentResponse> comments = commentService.getComments(PageRequest.of(0, 10));

        assertEquals(1, comments.getContent().size());
        assertEquals(commentResponse, comments.getContent().get(0));
    }

    @Test
    void testGetComment() {
        CommentService commentService = new CommentService(commentRepository, commentMapper);
        Comment comment = new Comment();
        CommentResponse commentResponse = new CommentResponse(null, null, null, null, null, null, null, null);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

        CommentResponse result = commentService.getComment(1L);

        assertEquals(commentResponse, result);
    }

    @Test
    void testAddComment() {
        CommentService commentService = new CommentService(commentRepository, commentMapper);
        Comment comment = new Comment();
        CommentResponse commentResponse = new CommentResponse(1L, 1L, new ArrayList<>(), 1L, "", false, false, null);
        CommentRequest commentRequest = new CommentRequest(1L, null, 1L,"");
        when(commentMapper.toEntity(any(CommentRequest.class), any(Comment.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

        CommentResponse result = commentService.addComment(commentRequest);

        assertEquals(commentResponse.postId(), commentRequest.postId());
        assertEquals(commentResponse.userId(), commentRequest.userId());
        assertEquals(commentResponse.content(), commentRequest.content());
    }

    @Test
    void testUpdateComment() {
        CommentService commentService = new CommentService(commentRepository, commentMapper);
        Comment comment = new Comment();
        CommentResponse commentResponse = new CommentResponse(null, null, null, null, null, null, null, null);
        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest(null);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

        CommentResponse result = commentService.updateComment(1L, commentUpdateRequest);

        assertEquals(commentResponse, result);
    }

    @Test
    void testDeleteComment() {
        CommentService commentService = new CommentService(commentRepository, commentMapper);
        Comment comment = new Comment();
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).findById(1L);
        assertEquals(true, comment.getIsDeleted());
    }
}