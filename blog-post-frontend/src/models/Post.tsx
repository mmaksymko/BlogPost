export interface PostRequest {
    title: string,
    content: string,
    headerImageURL: string
}

export interface PostResponse {
    id: number,
    title: string,
    content: string,
    authorId: number,
    postedAt: Date,
    headerImageURL: string
}

export interface SignedPost extends PostResponse {
    authorName: string
}