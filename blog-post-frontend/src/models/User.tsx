export enum UserRole {
    USER, ADMIN, SUPER_ADMIN
}

export interface UserUpdateRequest {
    firstName: string,
    lastName: string,
    email: string
}

export interface UserResponse {
    id: number,
    firstName: string,
    lastName: string,
    email: string,
    role: UserRole;
    registeredAt: Date;
    pfpUrl: string
}