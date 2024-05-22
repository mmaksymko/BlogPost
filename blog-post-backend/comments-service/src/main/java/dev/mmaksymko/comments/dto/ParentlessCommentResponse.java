package dev.mmaksymko.comments.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class ParentlessCommentResponse extends BaseCommentResponse {
    private List<ParentlessCommentResponse> subComments;

    public ParentlessCommentResponse(BaseCommentResponse base){
        super(base.getCommentId(), base.getPostId(), base.getUserId(), base.getContent(), base.getIsDeleted(), base.getIsModified(), base.getCommentedAt());
    }
}

