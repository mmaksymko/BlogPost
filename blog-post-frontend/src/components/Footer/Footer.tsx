import React from 'react';
import Facebook from '@mui/icons-material/Facebook';
import Telegram from '@mui/icons-material/Telegram';
import Instagram from '@mui/icons-material/Instagram';
import LinkedIn from '@mui/icons-material/LinkedIn';
import './Footer.css';
import { ReactComponent as FooterSvg } from '../../img/footer.svg';

const Header: React.FC = ({ }) => {
    var redirect = (url: string) => window.location.href = url;

    return (
        <footer className='footer'>
            <section className='footer-top-part'>
                <section className='footer-left-part'>
                    <span className='copyright'>© 2024</span> <span className="footer-title">BlogPost</span>
                </section>
                <section className="footer-middle-part">
                    <FooterSvg className="footer-svg" />
                </section>
                <section className='footer-right-part'>
                    <Facebook className="social-icon" style={{ fontSize: "2.25rem" }} onClick={() => redirect('https://www.facebook.com/mmaksymko')} />
                    <Telegram className="social-icon" style={{ fontSize: "2.25rem" }} onClick={() => redirect('https://t.me/maksymko')} />
                    <Instagram className="social-icon" style={{ fontSize: "2.25rem" }} onClick={() => redirect('https://instagram.com/mmaksymko')} />
                    <LinkedIn className="social-icon" style={{ fontSize: "2.25rem" }} onClick={() => redirect('https://www.linkedin.com/in/maksym-myna/')} />
                </section>
            </section>
        </footer>
    );
};

export default Header;