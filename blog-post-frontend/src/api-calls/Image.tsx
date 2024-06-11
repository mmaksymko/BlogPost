import axios from 'axios';

import { serverURL } from '../config';
import { Severity } from '../contexts/SnackBarContext';

export const addImage = async (headerImage: File, openSnack: (severity: Severity, message: string) => void): Promise<string | void> => {
    const imageFormData = new FormData()
    imageFormData.append('file', headerImage);
    return axios
        .post<string>(`${serverURL}/images-service/images/headers/`, imageFormData, { withCredentials: true })
        .then(response => response.data)
        .catch((error: any) => openSnack('error', `Помилка завантаження зображення ${error.response?.data.error}`));
};