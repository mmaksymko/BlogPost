package dev.mmaksymko.reactions.repositories.redis;

import dev.mmaksymko.reactions.models.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
}