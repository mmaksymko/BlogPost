import axios from 'axios';
import { serverURL } from '../config';
import { UserResponse, UserUpdateRequest } from '../models/User';
import { Severity } from '../contexts/SnackBarContext';

export const getCurrentUser = async (onSuccess: (response: any) => any, onError: (error: any) => any) => {
    return axios.get(`${serverURL}/current-user/`, { withCredentials: true })
        .then(onSuccess)
        .catch(onError);
}

export const getUser = async (id: number, onSuccess: (response: any) => any, onError: (error: any) => any) => {
    return axios.get(`${serverURL}/users-service/users/${id}/`, { withCredentials: true })
        .then(onSuccess)
        .catch(onError);
}

export const editUser = async (id: string | number, firstName: string, lastName: string, email: string, openSnack: (severity: Severity, message: string) => void): Promise<UserResponse | void> => {
    const postRequestBody: UserUpdateRequest = {
        firstName: firstName,
        lastName: lastName,
        email: email
    }

    return axios
        .put<UserResponse>(`${serverURL}/users-service/users/${id}/`, postRequestBody, { withCredentials: true })
        .then(response => {
            openSnack('success', 'Користувача успішно змінено!')
            return response.data;
        }).catch((error: any) => openSnack('error', `Помилка редагування ${error.response?.data.error}`))
}

export const changePfp = async (id: string | number, file: File, openSnack: (severity: Severity, message: string) => void): Promise<UserResponse | void> => {
    console.log(file);

    const imageFormData = new FormData()
    imageFormData.append('file', file);

    return axios
        .patch<UserResponse>(`${serverURL}/users-service/users/${id}/pfp/`, imageFormData, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка завантаження зображення ${error.response?.data.error}`));
}

export const logout = async () => axios.post(`${serverURL}/logout`, {}, { withCredentials: true });