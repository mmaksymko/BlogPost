export interface CommentRequest {
    postId: number,
    parentCommentId: number | null,
    content: string;
}

export interface CommentUpdateRequest {
    content: string
}

export interface CommentResponse {
    commentId: number;
    postId: number;
    userId: number;
    content: string;
    isDeleted: boolean;
    isModified: boolean;
    commentedAt: Date;
    parentComment: CommentResponse;
    subComments: CommentResponse[];
}

// export interface ChildlessCommentResponse extends BaseCommentResponse {
//     parentComment: ChildlessCommentResponse;
// }

// export interface ParentlessCommentResponse extends BaseCommentResponse {
//     subComments: ParentlessCommentResponse[];
// }

// export interface CommentResponse extends BaseCommentResponse {
// parentComment: CommentResponse | null;
// subComments: CommentResponse[];
// }

export interface SignedComment {
    commentId: number;
    postId: number;
    userId: number;
    content: string;
    isDeleted: boolean;
    isModified: boolean;
    commentedAt: Date | null;
    parentComment: SignedComment | null;
    subComments: SignedComment[];
    authorName: string;
    authorPfpUrl: string;
    likes: number;
    dislikes: number;
    myReaction: string | null;
}
