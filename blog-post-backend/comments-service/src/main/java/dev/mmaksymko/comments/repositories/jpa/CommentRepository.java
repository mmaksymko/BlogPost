package dev.mmaksymko.comments.repositories.jpa;

import dev.mmaksymko.comments.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPostIdAndParentCommentIsNull(Pageable pageable, Long postId);
}