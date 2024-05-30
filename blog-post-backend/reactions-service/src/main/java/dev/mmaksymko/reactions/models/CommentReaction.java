package dev.mmaksymko.reactions.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment_reaction")
public class CommentReaction {
    @EmbeddedId
    private CommentReactionId id;

    @ManyToOne
    @JoinColumn(name = "reaction_type_id", nullable = false)
    private ReactionType reactionType;

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentReactionId implements Serializable {
        @Column(name = "comment_id", nullable = false)
        private Long commentId;

        @Column(name = "user_id", nullable = false)
        private Long userId;
    }
}
