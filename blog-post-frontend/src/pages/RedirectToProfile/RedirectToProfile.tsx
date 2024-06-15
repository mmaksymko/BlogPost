import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const RedirectToProfile: React.FC = () => {
    const navigate = useNavigate();

    useEffect(() => {
        navigate('/profile');
    }, []);

    return (
        <></>
    );
}

export default RedirectToProfile;