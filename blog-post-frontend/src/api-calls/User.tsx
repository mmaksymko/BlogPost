import axios from 'axios';
import { serverURL } from '../config';

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

export const logout = async () => axios.post(`${serverURL}/logout`, {}, { withCredentials: true });