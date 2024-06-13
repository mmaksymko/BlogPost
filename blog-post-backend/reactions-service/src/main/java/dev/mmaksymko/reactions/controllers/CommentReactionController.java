package dev.mmaksymko.reactions.controllers;

import dev.mmaksymko.reactions.dto.CommentReactionRequest;
import dev.mmaksymko.reactions.dto.CommentReactionResponse;
import dev.mmaksymko.reactions.services.CommentReactionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reactions/comments/")
@AllArgsConstructor
public class CommentReactionController {
    private final CommentReactionService commentReactionService;

    @GetMapping("{commentId}/")
    public Page<CommentReactionResponse> getCommentReactions(@PathVariable Long commentId, Pageable pageable) {
        return commentReactionService.getCommentReactions(commentId, pageable);
    }

    @GetMapping("{commentId}/my-reaction/")
    public CommentReactionResponse getCommentReaction(@PathVariable Long commentId) {
        return commentReactionService.getCommentReaction(commentId);
    }

    @GetMapping("{commentId}/count/")
    public Map<String, Long> getCommentReactionsCount(@PathVariable Long commentId) {
        return commentReactionService.getCommentReactionsCount(commentId);
    }

    @PostMapping
    public CommentReactionResponse addCommentReaction(@RequestBody CommentReactionRequest commentReaction) {
        return commentReactionService.addCommentReaction(commentReaction);
    }

    @PutMapping("{commentId}/")
    public CommentReactionResponse updateCommentReaction(
            @RequestBody CommentReactionRequest commentReaction
    ) {
        return commentReactionService.updateCommentReaction(commentReaction);
    }

    @DeleteMapping("{commentId}/{userId}/")
    public void deleteCommentReaction(@PathVariable Long commentId, @PathVariable Long userId) {
        commentReactionService.deleteCommentReaction(commentId, userId);
    }
}
