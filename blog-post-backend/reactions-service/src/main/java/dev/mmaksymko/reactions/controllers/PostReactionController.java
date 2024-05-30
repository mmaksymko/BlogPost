package dev.mmaksymko.reactions.controllers;

import dev.mmaksymko.reactions.dto.PostReactionRequest;
import dev.mmaksymko.reactions.dto.PostReactionResponse;
import dev.mmaksymko.reactions.dto.ReactionTypeResponse;
import dev.mmaksymko.reactions.models.PostReaction;
import dev.mmaksymko.reactions.services.PostReactionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post-reactions/")
@AllArgsConstructor
public class PostReactionController {
    private final PostReactionService postReactionService;

    @GetMapping("{postId}/")
    public Page<PostReactionResponse> getPostReactions(@PathVariable Long postId, Pageable pageable) {
        return postReactionService.getPostReactions(postId, pageable);
    }

    @GetMapping("{postId}/{userId}/")
    public PostReactionResponse getPostReaction(@PathVariable Long postId, @PathVariable Long userId) {
        return postReactionService.getPostReaction(postId, userId);
    }

    @GetMapping("{postId}/count/")
    public Map<String, Long> getPostReactionsCount(@PathVariable Long postId) {
        return postReactionService.getPostReactionsCount(postId);
    }

    @PostMapping
    public PostReactionResponse addPostReaction(@RequestBody PostReactionRequest postReaction) {
        return postReactionService.addPostReaction(postReaction);
    }

    @PutMapping("{postId}/")
    public PostReactionResponse updatePostReaction(
            @RequestBody PostReactionRequest postReaction
    ) {
        return postReactionService.updatePostReaction(postReaction);
    }

    @DeleteMapping("{postId}/{userId}/")
    public void deletePostReaction(@PathVariable Long postId, @PathVariable Long userId) {
        postReactionService.deletePostReaction(postId, userId);
    }
}
