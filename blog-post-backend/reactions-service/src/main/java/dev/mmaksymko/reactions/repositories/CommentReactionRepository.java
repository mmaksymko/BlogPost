package dev.mmaksymko.reactions.repositories;

import dev.mmaksymko.reactions.models.CommentReaction;
import dev.mmaksymko.reactions.models.PostReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReaction.CommentReactionId> {
    Page<CommentReaction> findAllByIdCommentId(Long postId, Pageable pageable);
    @Query("SELECT cr.reactionType.name, COUNT(cr) FROM CommentReaction cr WHERE cr.id.commentId = :commentId GROUP BY cr.reactionType.name")
    Map<String, Long> countReactionsByType(Long commentId);
}
