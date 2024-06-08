import { ReactionTypeResponse } from './ReactionType'

export interface CommentReactionRequest {
    commentId: number,
    reaction: string
}

export interface CommentReactionResponse {
    commentId: number,
    userId: number,
    reaction: ReactionTypeResponse
}