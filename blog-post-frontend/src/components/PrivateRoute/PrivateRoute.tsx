import React, { useContext, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { AuthContext, unauthorizedUser } from '../../contexts/AuthContext';
import axios from 'axios';
import { serverURL } from '../../config';
import { UserRole } from '../../models/User';

interface PrivateRouteProps {
    path: string;
    element: React.ReactElement;
    requiredRole: UserRole;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ requiredRole, element, path }) => {
    const { role, id, fetchUser, handleLogin, setUser } = useContext(AuthContext);
    const [isAuthorized, setIsAuthorized] = React.useState(false);
    const location = useLocation()
    const navigate = useNavigate();

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const response = await axios.get(`${serverURL}/current-user/`, { withCredentials: true });
                setUser({ ...response.data });
                const role: UserRole = response.data.role
                const authorized = (role === UserRole.SUPER_ADMIN) || (role === UserRole.ADMIN && requiredRole !== UserRole.SUPER_ADMIN) ||
                    (role === UserRole.USER && (requiredRole === UserRole.USER || requiredRole === UserRole.UNAUTHORIZED)) ||
                    (response.data.role === UserRole.UNAUTHORIZED && requiredRole === UserRole.UNAUTHORIZED)
                if (!authorized) {
                    navigate('/login')
                }
                setIsAuthorized(authorized);
            } catch (error: any) {
                if (error.response && error.response.data.error === 'No value present') {
                    setUser(unauthorizedUser);
                } else {
                    console.error('Failed to check auth:', error);
                }
            }
        };

        checkAuth();

    }, []);

    return isAuthorized ? element : null;
};

export default PrivateRoute;