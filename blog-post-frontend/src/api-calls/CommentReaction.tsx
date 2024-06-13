import { serverURL } from "../config";
import axios from "axios";
import { Severity } from "../contexts/SnackBarContext";
import { CommentReactionRequest, CommentReactionResponse } from "../models/Reaction/CommentReaction";

export const getUserReaction = async (id: string | number, openSnack: (severity: Severity, message: string) => void): Promise<CommentReactionResponse> => {
    return axios
        .get(`${serverURL}/reactions-service/reactions/comments/${id}/my-reaction/`, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка отримання ${error.response?.data.error}`));
}

export const getCommentReactionCount = async (id: string | number, openSnack: (severity: Severity, message: string) => void) => {
    return axios
        .get(`${serverURL}/reactions-service/reactions/comments/${id}/count/`, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка отримання ${error.response?.data.error}`));
}

export const addCommentReaction = async (id: number, reaction: string, openSnack: (severity: Severity, message: string) => void): Promise<CommentReactionResponse | void> => {
    const postRequestBody: CommentReactionRequest = {
        commentId: id,
        reaction: reaction
    }

    return axios
        .post<CommentReactionResponse>(`${serverURL}/reactions-service/reactions/comments/`, postRequestBody, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка створення ${error.response?.data.error}`))
}

export const deleteCommentReaction = async (id: number, openSnack: (severity: Severity, message: string) => void): Promise<CommentReactionResponse | void> => {
    return axios
        .delete<CommentReactionResponse>(`${serverURL}/reactions-service/reactions/comments/${id}/`, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка створення ${error.response?.data.error}`))
}
