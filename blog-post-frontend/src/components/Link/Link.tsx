import React from 'react';
import './Link.css';
import { Link as RouterLink, useNavigate } from 'react-router-dom';

interface LinkProps {
    to: string;
    className?: string;
    children: React.ReactNode;
    ariaLabel?: string;
}

const Link: React.FC<LinkProps> = ({ to, className, children, ariaLabel }) => {
    const navigate = useNavigate();

    return (
        <RouterLink
            className={`link${className ? ` ${className}` : ''}`}
            to={to}
            aria-label={ariaLabel}
            onClick={
                (event) => {
                    if (!event.ctrlKey) {
                        event.preventDefault();
                        navigate(to);
                        window.scrollTo(0, 0)
                    }
                }
            }
        >
            {children}
        </RouterLink>
    )
};

export default Link;