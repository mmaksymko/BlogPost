package dev.mmaksymko.reactions.repositories;

import dev.mmaksymko.reactions.dto.ReactionCount;
import dev.mmaksymko.reactions.models.CommentReaction;
import dev.mmaksymko.reactions.models.PostReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Stream;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReaction.CommentReactionId> {
    Page<CommentReaction> findAllByIdCommentId(Long postId, Pageable pageable);
    @Query("SELECT new dev.mmaksymko.reactions.dto.ReactionCount(cr.reactionType.name, COUNT(cr)) FROM CommentReaction cr WHERE cr.id.commentId = :commentId GROUP BY cr.reactionType.name")
    Stream<ReactionCount> countReactionsByType(Long commentId);
}
