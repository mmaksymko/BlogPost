import { serverURL } from "../config";
import axios from "axios";
import { Severity } from "../contexts/SnackBarContext";
import { PostReactionRequest, PostReactionResponse } from "../models/Reaction/PostReaction";

export const getUserPostReaction = async (id: string | number, openSnack: (severity: Severity, message: string) => void): Promise<PostReactionResponse> => {
    return axios
        .get(`${serverURL}/reactions-service/reactions/posts/${id}/my-reaction/`, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка отримання ${error.response?.data.error}`));
}

export const getPostReactionCount = async (id: string | number, openSnack: (severity: Severity, message: string) => void) => {
    return axios
        .get(`${serverURL}/reactions-service/reactions/posts/${id}/count/`, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка отримання ${error.response?.data.error}`));
}

export const addPostReaction = async (id: number, reaction: string, openSnack: (severity: Severity, message: string) => void): Promise<PostReactionResponse | void> => {
    const postRequestBody: PostReactionRequest = {
        postId: id,
        reaction: reaction
    }

    return axios
        .post<PostReactionResponse>(`${serverURL}/reactions-service/reactions/posts/`, postRequestBody, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка створення ${error.response?.data.error}`))
}

export const deletePostReaction = async (id: number, openSnack: (severity: Severity, message: string) => void): Promise<PostReactionResponse | void> => {
    return axios
        .delete<PostReactionResponse>(`${serverURL}/reactions-service/reactions/posts/${id}/`, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка створення ${error.response?.data.error}`))
}
