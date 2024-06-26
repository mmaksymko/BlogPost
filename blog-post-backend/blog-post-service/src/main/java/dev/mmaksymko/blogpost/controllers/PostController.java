package dev.mmaksymko.blogpost.controllers;

import dev.mmaksymko.blogpost.dto.PostRequest;
import dev.mmaksymko.blogpost.dto.PostResponse;
import dev.mmaksymko.blogpost.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public Page<PostResponse> getPosts(
            Pageable pageable,
            @RequestParam(required = false) Long authorId
    ) {
        return postService.getPosts(pageable, authorId);
    }

    @GetMapping("{id}/")
    public PostResponse getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @PostMapping
    public PostResponse addPost(@RequestBody PostRequest request) {
        return postService.addPost(request);
    }

    @PutMapping("{id}/")
    public PostResponse updatePost(@PathVariable Long id, @RequestBody PostRequest request) {
        return postService.updatePost(id, request);
    }

    @DeleteMapping("{id}/")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}
