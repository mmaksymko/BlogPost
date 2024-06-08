import { ReactionTypeResponse } from './ReactionType'

export interface PostReactionRequest {
    postId: number,
    reaction: string
}

export interface PostReactionResponse {
    postId: number,
    userId: number,
    reaction: ReactionTypeResponse
}