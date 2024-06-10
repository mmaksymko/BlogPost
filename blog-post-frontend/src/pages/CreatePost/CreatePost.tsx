import React, { useEffect, useState, useRef, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import './CreatePost.css';

import Button from '../../components/Button';
import PreviewIcon from '@mui/icons-material/Preview';
import ReactMarkdown from 'react-markdown';
import axios from 'axios';
import { SnackBarContext, defaultSnackBar, Severity } from '../../contexts/SnackBarContext';
import { PostRequest, PostResponse } from '../../models/Post';
import { serverURL } from '../../config';

const CreatePost: React.FC = () => {
    const fileInputRef = useRef<HTMLInputElement>(null);
    const navigate = useNavigate();

    const { setSnackBar } = useContext(SnackBarContext);

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [headerImage, setHeaderImage] = useState<File | null>(null);
    const [headerImageUrl, setHeaderImageUrl] = useState<string | null>(null);

    const [showPreview, setShowPreview] = useState<boolean | null>(null);
    const [outlined, setOutlined] = useState(true);
    const [inverted, setInverted] = useState(false);

    useEffect(() => {
        wrapPreviewButton()
    }, []);

    const openSnack = (severity: Severity, message: string) => {
        setSnackBar({ ...defaultSnackBar, open: true, severity: severity, message: message });
    }

    const wrapPreviewButton = () => {
        const isWrapped = isContentWrapped('create-post-content');

        const previewButtonContainer = document.getElementsByClassName('preview-button-container')[0];
        previewButtonContainer.classList.remove('preview-button-right', 'preview-button-below')

        previewButtonContainer.classList.add(isWrapped ? 'preview-button-below' : 'preview-button-right');
    }

    const isContentWrapped = (className: string): boolean => {
        const element = document.querySelector(`.${className}`);
        if (!element) return false;

        let totalChildWidth = 0;
        const children = element.children;
        for (let i = 0; i < children.length; i++) {
            if (!children[i].classList.contains('invisible')) {
                const childStyle = window.getComputedStyle(children[i]);
                const marginLeft = parseFloat(childStyle.marginLeft);
                const marginRight = parseFloat(childStyle.marginRight);
                totalChildWidth += children[i].getBoundingClientRect().width - marginLeft - marginRight;
            }
        }

        return totalChildWidth > element.getBoundingClientRect().width;
    };

    const handlePreviewButtonClick = () => {
        setShowPreview(!showPreview ?? true);
        setOutlined(!outlined);
        setInverted(!inverted);

        wrapPreviewButton()
    }

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0) {
            setHeaderImage(e.target.files[0]);
            setHeaderImageUrl(URL.createObjectURL(e.target.files[0]));
        }
    }

    const createPost = async () => {
        if (!headerImage) return;

        try {
            const imageFormData = new FormData()
            imageFormData.append('file', headerImage);
            const imageResponse = await axios.post<string>(`${serverURL}/images-service/images/headers/`, imageFormData, { withCredentials: true });

            const postRequestBody: PostRequest = {
                title: title,
                content: content,
                headerImageURL: imageResponse.data
            }

            const response = await axios.post<PostResponse>(`${serverURL}/blog-post-service/posts/`, postRequestBody, { withCredentials: true });
            openSnack('success', 'Пост успішно створено!');
            navigate('/posts/' + response.data.id);
        } catch (error: any) {
            console.log(error.response?.data)
            console.error('Error fetching data: ', error);
            openSnack('error', `Помилка створення ${error.response?.data.error}`);
        }
    }

    return (
        <div className='create-post-container'>
            <div className='page-header-title'>
                Створення поста
            </div>
            <input
                ref={fileInputRef}
                accept="image/*"
                style={{ display: 'none' }}
                id="raised-button-file"
                type="file"
                onChange={handleFileChange}
            />
            <div className='create-post-section-title'>
                Заголовкове фото:
            </div>
            <div className='select-post-header-image'>
                <Button inverted onClick={() => fileInputRef.current?.click()}>
                    Обрати зображення
                </Button>
                {headerImageUrl && <img className='post-header-image-preview' src={headerImageUrl} alt="Header preview" />}
            </div>

            <div className='create-post-section-title'>
                Заголовок:
            </div>
            <div className={`${showPreview ? 'spread ' : ''}create-post-content`}>
                <textarea
                    className='header-textarea centered post-textarea'
                    maxLength={255}
                    onChange={e => setTitle(e.target.value)}
                />
                <div className='invisible' aria-disabled />
            </div>
            <div className='create-post-section-title'>
                Вміст:
            </div>
            <div className={`${showPreview ? 'spread ' : ''}create-post-content`}>
                <div className={`${!showPreview ? `centered ${showPreview !== null ? 'animate-centered ' : ''}` : ''}post-textarea-container`}>
                    <textarea
                        className='post-textarea'
                        value={content}
                        onChange={e => setContent(e.target.value)}
                    />
                    <div className="preview-button-container">
                        <Button
                            outlined={outlined}
                            inverted={inverted}
                            onClick={handlePreviewButtonClick}
                            width='3rem'
                            height='3rem'
                        >
                            <PreviewIcon sx={{ height: "2.5rem", width: "2.5rem" }} />
                        </Button>
                    </div>
                </div>

                <ReactMarkdown
                    className={`${!showPreview ? 'invisible ' : ''}post-preview`}
                >
                    {content}
                </ReactMarkdown>
            </div >
            <div className='create-post-button'>
                <Button inverted onClick={createPost}>
                    Створити
                </Button>
            </div>
        </div>
    );
};

export default CreatePost;