package dev.mmaksymko.reactions.repositories.redis;

import dev.mmaksymko.reactions.models.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
}