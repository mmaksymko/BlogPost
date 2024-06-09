export enum UserRole {
    SUPER_ADMIN, ADMIN, USER, UNAUTHORIZED
}

export interface UserUpdateRequest {
    firstName: string,
    lastName: string,
    email: string
}

export interface UserResponse {
    id: number | undefined,
    firstName: string | undefined,
    lastName: string | undefined,
    email: string | undefined,
    role: UserRole | undefined;
    registeredAt: Date | undefined;
    pfpUrl: string | undefined
}

export interface CurrentUser {
    id: number | undefined,
    firstName: string | undefined,
    lastName: string | undefined,
    email: string | undefined,
    role: UserRole | undefined;
    pfpUrl: string | undefined
}