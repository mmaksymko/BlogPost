import React from 'react';

export type Severity = "success" | "info" | "warning" | "error";

export interface SnackBarState {
    open: boolean;
    severity: Severity;
    message?: string;
}

interface AuthContextType extends SnackBarState {
    setSnackBar: React.Dispatch<React.SetStateAction<SnackBarState>>,
}

export const defaultSnackBar: SnackBarState = {
    open: false,
    severity: "success",
    message: "Success!",
};

const SnackBarContext = React.createContext<AuthContextType>({
    ...defaultSnackBar,
    setSnackBar: () => { },
});

export { SnackBarContext };