import React, { useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext, unauthorizedUser } from '../../contexts/AuthContext';
import axios from 'axios';
import { serverURL } from '../../config';
import { UserRole } from '../../models/User';

interface PrivateRouteProps {
    element: React.ReactElement;
    requiredRole: UserRole;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ requiredRole, element }) => {
    const { setUser } = useContext(AuthContext);
    const [isAuthorized, setIsAuthorized] = React.useState(false);
    const navigate = useNavigate();

    var checkRole = (role: UserRole) => {
        if (requiredRole === UserRole.UNAUTHORIZED) {
            return true;
        }

        if (role === UserRole.UNAUTHORIZED) {
            return false;
        }

        if (role === UserRole.SUPER_ADMIN) {
            return true;
        }

        if (role === UserRole.ADMIN && requiredRole !== UserRole.SUPER_ADMIN) {
            return true;
        }

        if (requiredRole === UserRole.USER && requiredRole === UserRole.USER) {
            return true;
        }

        return false;
    }

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const response = await axios.get(`${serverURL}/current-user/`, { withCredentials: true });
                setUser({ ...response.data });
                const role: UserRole = response.data.role
                const authorized = checkRole(role)
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