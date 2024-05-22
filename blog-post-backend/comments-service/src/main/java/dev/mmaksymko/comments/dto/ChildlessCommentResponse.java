package dev.mmaksymko.comments.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class ChildlessCommentResponse extends BaseCommentResponse {
    private ChildlessCommentResponse parentComment;

    public ChildlessCommentResponse(BaseCommentResponse base){
        super(base.getCommentId(), base.getPostId(), base.getUserId(), base.getContent(), base.getIsDeleted(), base.getIsModified(), base.getCommentedAt());
    }
}
