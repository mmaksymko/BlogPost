import React from 'react';
import Facebook from '@mui/icons-material/Facebook';
import Telegram from '@mui/icons-material/Telegram';
import Instagram from '@mui/icons-material/Instagram';
import LinkedIn from '@mui/icons-material/LinkedIn';
import './Footer.css';
import { ReactComponent as FooterSvg } from '../../img/footer.svg';

const Header: React.FC = ({ }) => {

    return (
        <footer className='footer'>
            <section className='footer-left-part'>
                <span className='copyright'>© 2024</span> <span className="footer-title">BlogPost</span>
            </section>
            <section className="footer-middle-part">
                <FooterSvg className="footer-svg" />
            </section>
            <section className='footer-right-part'>
                <Facebook className="social-icon" style={{ fontSize: "2.25rem" }} onClick={() => window.open('https://www.facebook.com/mmaksymko')} />
                <Telegram className="social-icon" style={{ fontSize: "2.25rem" }} onClick={() => window.open('https://t.me/maksymko')} />
                <Instagram className="social-icon" style={{ fontSize: "2.25rem" }} onClick={() => window.open('https://instagram.com/mmaksymko')} />
                <LinkedIn className="social-icon" style={{ fontSize: "2.25rem" }} onClick={() => window.open('https://www.linkedin.com/in/maksym-myna/')} />
            </section>
        </footer>
    );
};

export default Header;