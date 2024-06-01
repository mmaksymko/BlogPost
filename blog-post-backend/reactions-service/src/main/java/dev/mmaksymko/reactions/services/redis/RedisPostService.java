package dev.mmaksymko.reactions.services.redis;

import dev.mmaksymko.reactions.models.Post;
import dev.mmaksymko.reactions.repositories.redis.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisPostService {
    private final PostRepository postRepository;

    public Post getPost(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Post post) {
        return savePost(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}