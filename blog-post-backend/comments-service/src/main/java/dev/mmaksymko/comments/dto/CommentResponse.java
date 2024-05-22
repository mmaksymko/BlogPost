package dev.mmaksymko.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
public class CommentResponse extends BaseCommentResponse {
    private ChildlessCommentResponse parentComment;
    private List<ParentlessCommentResponse> subComments;

    public CommentResponse(BaseCommentResponse base){
        super(base.getCommentId(), base.getPostId(), base.getUserId(), base.getContent(), base.getIsDeleted(), base.getIsModified(), base.getCommentedAt());
    }
}
