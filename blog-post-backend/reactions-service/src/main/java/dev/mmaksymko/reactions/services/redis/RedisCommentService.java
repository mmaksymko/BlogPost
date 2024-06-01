package dev.mmaksymko.reactions.services.redis;

import dev.mmaksymko.reactions.models.Comment;
import dev.mmaksymko.reactions.repositories.redis.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisCommentService {
    private final CommentRepository postRepository;

    public Comment getComment(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Comment saveComment(Comment comment) {
        return postRepository.save(comment);
    }

    public Comment updateComment(Comment comment) {
        return saveComment(comment);
    }

    public void deleteComment(Long id) {
        postRepository.deleteById(id);
    }
}