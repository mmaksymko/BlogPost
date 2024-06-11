import React, { useContext, useEffect } from 'react';
import { useLocation } from 'react-router-dom';

import './Header.css';

import LoginIcon from '@mui/icons-material/Login';
import FaceIcon from '@mui/icons-material/Face';

import { UserRole } from '../../models/User';
import { AuthContext, unauthorizedUser } from '../../contexts/AuthContext';
import Link from '../Link';
import Button from '../Button';
import { getCurrentUser } from '../../api-calls/User';

const Header: React.FC = () => {
    const location = useLocation()
    const { role, pfpUrl, handleLogin, setUser } = useContext(AuthContext);

    const handleLoginClick = () => {
        handleLogin(location.pathname);
    };

    useEffect(() => {
        const checkAuth = async () => {
            const onSuccess = (response: any) => response.data;
            const onError = () => setUser(unauthorizedUser);

            const response = await getCurrentUser(onSuccess, onError);
            if (response) {
                setUser({ ...response });
            }
        };

        checkAuth();
    }, []);

    return (
        <>
            <header className="header">
                <div className="title-container">
                    <Link to="/" className="title">
                        BlogPost
                    </Link>
                </div>
                <section className="profile-login-container">
                    {!role || role !== UserRole.UNAUTHORIZED
                        ?
                        <div className="profile-text-container">
                            <Link to='/create-post'> <Button width='8rem' height='3rem' margin="0 1rem 0 0" outlined onClick={() => { }}>Додати</Button></Link>
                            {
                                !pfpUrl ?
                                    <FaceIcon style={{ width: "2rem", height: "2rem" }} />
                                    :
                                    <img className="pfp" src={pfpUrl} alt="Profile" />
                            }
                            <Link to="/profile" className="profile-text">Профіль</Link>
                        </div>
                        :
                        <div className="profile-text-container">
                            <LoginIcon style={{ width: "2rem", height: "2rem" }} onClick={handleLoginClick} />
                            <p className="profile-text" onClick={handleLoginClick}>Увійти</p>
                        </div>
                    }
                </section >
            </header >
        </>
    );
};

export default Header;