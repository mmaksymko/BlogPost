package dev.mmaksymko.reactions.repositories.jpa;

import dev.mmaksymko.reactions.dto.ReactionCount;
import dev.mmaksymko.reactions.models.PostReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, PostReaction.PostReactionId> {
    Page<PostReaction> findAllByIdPostId(Long postId, Pageable pageable);
    @Query("SELECT new dev.mmaksymko.reactions.dto.ReactionCount(pr.reactionType.name, COUNT(pr)) FROM PostReaction pr WHERE pr.id.postId = :postId GROUP BY pr.reactionType.name")
    Stream<ReactionCount> countReactionsByType(Long postId);
}
