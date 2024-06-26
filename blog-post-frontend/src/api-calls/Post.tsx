import axios from 'axios';
import { serverURL } from '../config';
import { Severity } from '../contexts/SnackBarContext';
import { PostRequest, PostResponse } from '../models/Post';
import { Page } from '../models/Page';

export const getPosts = async (
    page: number,
    openSnack: (severity: Severity, message: string) => void,
    authorId?: number
): Promise<Page<PostResponse>> => {
    let url = `${serverURL}/blog-post-service/posts/?size=10&page=${page}`;
    if (authorId) url += `&authorId=${authorId}`;

    return axios
        .get(url, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка отримання ${error.response?.data.error}`));
}

export const getPost = async (id: string | number): Promise<PostResponse> => {
    return axios
        .get(`${serverURL}/blog-post-service/posts/${id}/`, { withCredentials: true })
        .then(response => response.data)
        .catch(() => null);
}

export const addPost = async (title: string, content: string, headerImageURL: string, openSnack: (severity: Severity, message: string) => void): Promise<PostResponse | void> => {
    const postRequestBody: PostRequest = {
        title: title,
        content: content,
        headerImageURL: headerImageURL
    }

    return axios
        .post<PostResponse>(`${serverURL}/blog-post-service/posts/`, postRequestBody, { withCredentials: true })
        .then(response => {
            openSnack('success', 'Пост успішно створено!')
            return response.data;
        }).catch((error: any) => openSnack('error', `Помилка створення ${error.response?.data.error}`))
}

export const updatePost = async (id: string | number, title: string, content: string, headerImageURL: string, openSnack: (severity: Severity, message: string) => void): Promise<PostResponse | void> => {
    const postRequestBody: PostRequest = {
        title: title,
        content: content,
        headerImageURL: headerImageURL
    }

    return axios
        .put<PostResponse>(`${serverURL}/blog-post-service/posts/${id}/`, postRequestBody, { withCredentials: true })
        .then(response => {
            openSnack('success', 'Пост успішно відредаговано!')
            return response.data;
        }).catch((error: any) => openSnack('error', `Помилка створення ${error.response?.data.error}`))
}

export const deletePost = async (id: number, openSnack: (severity: Severity, message: string) => void): Promise<void> => {
    return axios
        .delete(`${serverURL}/blog-post-service/posts/${id}/`, { withCredentials: true })
        .then(response => {
            openSnack('success', 'Пост успішно видалено!')
            return response.data;
        }).catch((error: any) => openSnack('error', `Помилка видалення ${error.response?.data.error}`))
}