export interface CommentRequest {
    postId: number,
    parentCommentId: number | null,
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
    parentComment: ChildlessCommentResponse | null;
    subComments: ParentlessCommentResponse[];
}

interface Signed {
    authorName: string;
    authorPfpUrl: string;
}

export interface SignedBaseCommentResponse extends BaseCommentResponse, Signed {
    likes: number;
    dislikes: number;
    myReaction: string | null;
}

export interface SignedChildlessCommentResponse extends SignedBaseCommentResponse {
    parentComment: SignedChildlessCommentResponse | null;
}

export interface SignedParentlessCommentResponse extends SignedBaseCommentResponse {
    subComments: SignedParentlessCommentResponse[];
}

export interface SignedComment extends SignedBaseCommentResponse {
    parentComment: SignedChildlessCommentResponse | null;
    subComments: SignedParentlessCommentResponse[];
}