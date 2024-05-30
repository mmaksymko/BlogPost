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
@Table(name = "post_reaction")
public class PostReaction {
    @EmbeddedId
    private PostReactionId id;

    @ManyToOne
    @JoinColumn(name = "reaction_type_id", nullable = false)
    private ReactionType reactionType;

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostReactionId implements Serializable {
        @Column(name = "post_id", nullable = false)
        private Long postId;

        @Column(name = "user_id", nullable = false)
        private Long userId;
    }
}
