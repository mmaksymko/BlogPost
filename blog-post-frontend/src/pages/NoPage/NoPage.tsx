import React from "react";
import './NoPage.css';

const NoPage: React.FC = () => {

    return (
        <>
            <h1 className="page-not-found-header">Такої сторінки не існує 😿</h1>
            <div className="no-page-image-container">
                <img className="no-page-image" src="https://i.imgflip.com/2pg2s7.jpg?a475896"></img>
            </div>
        </>
    )
}

export default NoPage;