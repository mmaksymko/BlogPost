import { UserResponse } from "./User";

export interface CommentRequest {
    postId: number,
    parentCommentId: number,
    content: string;
}

export interface CommentUpdateRequest {
    content: string
}

export interface BaseCommentResponse {
    commentId: number;
    postId: number;
    userId: number;
    content: string;
    isDeleted: boolean;
    isModified: boolean;
    commentedAt: Date;
}

export interface ChildlessCommentResponse extends BaseCommentResponse {
    parentComment: ChildlessCommentResponse;
}

export interface ParentlessCommentResponse extends BaseCommentResponse {
    subComments: ParentlessCommentResponse[];
}

export interface CommentResponse extends BaseCommentResponse {
    parentComment: ChildlessCommentResponse;
    subComments: ParentlessCommentResponse[];
}

export interface SignedComment extends CommentResponse {
    user: UserResponse;
}