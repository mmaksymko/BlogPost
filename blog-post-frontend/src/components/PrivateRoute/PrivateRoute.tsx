import React, { useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext, unauthorizedUser } from '../../contexts/AuthContext';
import { UserRole } from '../../models/User';
import { getCurrentUser } from '../../api-calls/User';

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
            const onSuccess = (response: any) => response.data;
            const onError = (error: any) => {
                if (error.response && error.response.data.error === 'No value present') {
                    setUser(unauthorizedUser);
                } else {
                    console.error('Failed to check auth:', error);
                }
            }
            const response = await getCurrentUser(onSuccess, onError);

            setUser({ ...response });
            const role: UserRole = response.role
            const authorized = checkRole(role)
            if (!authorized) {
                navigate('/login')
            }
            setIsAuthorized(authorized);
        };

        checkAuth();

    }, []);

    return isAuthorized ? element : null;
};

export default PrivateRoute;