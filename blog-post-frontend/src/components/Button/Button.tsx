import React from 'react';
import './Button.css';
import EastIcon from '@mui/icons-material/East';
interface ButtonProps {
    onClick?: () => void;
    width?: string;
    height?: string;
    children?: React.ReactNode;
    inverted?: boolean;
    outlined?: boolean;
    disabled?: boolean;
    margin?: string;
    noshadow?: boolean;
}

const Button: React.FC<ButtonProps> = ({ width, height = '4rem', onClick, children, inverted = false, disabled = false, outlined = false, margin = '0rem', noshadow = false }) => {
    const style = { width, height, margin };

    let className = 'button';

    if (inverted) {
        className += ' button-inverted';
    } else if (outlined) {
        className += ' button-outlined';
        if (noshadow) {
            className += ' button-no-shadow';
        }
    }
    return (
        <button
            onClick={onClick}
            className={className}
            style={style}
            disabled={disabled}>
            {children}
            {/* <EastIcon /> */}
        </button>
    );
};

export default Button;