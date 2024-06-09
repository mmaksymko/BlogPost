import React from 'react';
import { serverURL } from '../../config';
import { CurrentUser, UserRole } from '../../models/User';

export interface UserState extends CurrentUser {
    handleLogin: (pathname: string) => void,
    fetchUser(): void
}

interface AuthContextType extends UserState {
    setUser: React.Dispatch<React.SetStateAction<UserState>>,
    handleLogin: (pathname: string) => void,
    fetchUser(): void
}

export const unauthorizedUser: UserState = {
    id: undefined,
    role: UserRole.UNAUTHORIZED,
    firstName: undefined,
    lastName: undefined,
    email: undefined,
    pfpUrl: undefined,
    handleLogin: (pathname: string) => {
        localStorage.setItem('pathname', pathname);
        window.location.href = `${serverURL}/oauth2/authorization/google`;
    },
    fetchUser: () => { }
};

const AuthContext = React.createContext<AuthContextType>({
    ...unauthorizedUser,
    setUser: () => { },
});

export { AuthContext };