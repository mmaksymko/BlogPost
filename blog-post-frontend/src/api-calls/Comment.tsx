import axios from 'axios';
import { serverURL } from '../config';
import { Severity } from '../contexts/SnackBarContext';
import { Page } from '../models/Page';
import { CommentRequest, CommentResponse, CommentUpdateRequest } from '../models/Comment';

export const getComments = async (postId: string | number, page: number, openSnack: (severity: Severity, message: string) => void): Promise<Page<CommentResponse>> => {
    return axios
        .get(`${serverURL}/comments-service/comments/?postId=${postId}&size=10&page=${page}`, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка отримання ${error.response?.data.error}`));
}

export const addComment = async (postId: number, parentCommentId: number | null, content: string, openSnack: (severity: Severity, message: string) => void): Promise<CommentResponse | void> => {
    const postRequestBody: CommentRequest = {
        postId: postId,
        parentCommentId: parentCommentId,
        content: content
    }

    return axios
        .post<CommentResponse>(`${serverURL}/comments-service/comments/`, postRequestBody, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка створення ${error.response?.data.error}`))
}

export const updateComment = async (id: number | string, content: string, openSnack: (severity: Severity, message: string) => void): Promise<CommentResponse | void> => {
    const putRequestBody: CommentUpdateRequest = {
        content: content
    }

    return axios
        .put<CommentResponse>(`${serverURL}/comments-service/comments/${id}/`, putRequestBody, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка редагування ${error.response?.data.error}`))
}

export const deleteComment = async (id: number, openSnack: (severity: Severity, message: string) => void): Promise<CommentResponse | void> => {
    return axios
        .delete<CommentResponse>(`${serverURL}/comments-service/comments/${id}/`, { withCredentials: true })
        .then(response => {
            openSnack('success', 'Коментар успішно видалено!')
            return response.data;
        }).catch((error: any) => openSnack('error', `Помилка видалення ${error.response?.data.error}`))
}