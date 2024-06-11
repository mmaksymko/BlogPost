import React, { useContext, useEffect } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import { useLocation, useNavigate } from 'react-router-dom';
import { getCurrentUser } from '../../api-calls/User';

const Login: React.FC = () => {
    const { handleLogin } = useContext(AuthContext);
    const location = useLocation()
    const navigate = useNavigate();

    useEffect(() => {
        const checkAuth = async () => {
            const onSuccess = () => navigate('/profile');
            const onError = () => handleLogin(location.pathname);

            await getCurrentUser(onSuccess, onError);
        };

        checkAuth();
    }, []);

    return (
        <></>
    )
}

export default Login;
