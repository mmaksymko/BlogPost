
import React, { useContext, useEffect } from 'react';

import { useNavigate } from 'react-router-dom';

const OAauthRedirectHandler: React.FC = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const fetch = async () => {
            const prevLocation = localStorage.getItem('pathname');
            if (prevLocation) {
                navigate(prevLocation);
            } else {
                navigate('/');
            }
        };

        fetch();
    }, []);

    return (
        <div>Успішно!</div>
    )
}

export default OAauthRedirectHandler;