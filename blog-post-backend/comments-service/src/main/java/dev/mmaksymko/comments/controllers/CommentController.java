package dev.mmaksymko.comments.controllers;

import dev.mmaksymko.comments.dto.*;
import dev.mmaksymko.comments.services.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments/")
@AllArgsConstructor
public class CommentController {
    public final CommentService commentService;

    @GetMapping
    public Page<CommentResponse> getComments(
            @RequestParam(required = false) Long postId,
            Pageable pageable
    ) {
        return commentService.getComments(postId, pageable);
    }

    @GetMapping("{id}/")
    public CommentResponse getComment(@PathVariable Long id) {
        return commentService.getComment(id);
    }

    @GetMapping("{id}/base/")
    public BaseCommentResponse getCommentByItself(@PathVariable Long id) {
        return commentService.getCommentByItself(id);
    }

    @PostMapping
    public CommentResponse addComment(@RequestBody CommentRequest request) {
        return commentService.addComment(request);
    }

    @PutMapping("{id}/")
    public CommentResponse updateComment(@PathVariable Long id, @RequestBody CommentUpdateRequest request) {
        return commentService.updateComment(id, request);
    }

    @DeleteMapping("{id}/")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

}
