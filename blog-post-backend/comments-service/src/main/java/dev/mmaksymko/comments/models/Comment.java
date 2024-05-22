package dev.mmaksymko.comments.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @OneToMany(mappedBy = "parentComment")
    @Builder.Default
    private List<Comment> subComments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="parent_comment_id", referencedColumnName="comment_id")
    @JsonBackReference
    private Comment parentComment;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "is_modified", nullable = false)
    @Builder.Default
    private Boolean isModified = false;

    @Column(name = "commented_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime commentedAt = LocalDateTime.now();
}
