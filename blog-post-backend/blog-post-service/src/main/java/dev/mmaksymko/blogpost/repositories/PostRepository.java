package dev.mmaksymko.blogpost.repositories;

import dev.mmaksymko.blogpost.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByAuthorId(Long authorId, Pageable pageable);
}
