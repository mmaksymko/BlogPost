import React from 'react';

import LoginIcon from '@mui/icons-material/Login';
import Link from '../Link';
import './Header.css';
import { useLocation } from 'react-router-dom';

export interface HeaderProps {
    currentPage?: 'works' | 'publishers' | 'authors' | 'subjects';
    postButton?: boolean;
}

const Header: React.FC<HeaderProps> = ({ currentPage, postButton }) => {
    const location = useLocation()

    return (
        <>
            <header className="header">
                <div className="title-container">
                    <Link to="/" className="title">
                        BlogPost
                    </Link>
                </div>
                <section className="profile-login-container">
                    <div className="profile-text-container">
                        <LoginIcon style={{ width: "2rem", height: "2rem" }} />
                        <p className="profile-text">Увійти</p>
                    </div>
                </section >
            </header >
        </>
    );
};

export default Header;