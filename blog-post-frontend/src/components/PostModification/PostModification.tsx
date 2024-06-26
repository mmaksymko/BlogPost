import React, { useEffect, useState, useRef, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import './PostModification.css';

import Button from '../Button';
import PreviewIcon from '@mui/icons-material/Preview';
import { SnackBarContext, defaultSnackBar, Severity } from '../../contexts/SnackBarContext';
import { addPost, updatePost } from '../../api-calls/Post';
import { addImage } from '../../api-calls/Image';
import Markdown from '../Markdown';

interface PostModificationProps {
    isEdit?: boolean;
    imageUrl?: string;
    editContent?: string;
    editTitle?: string;
    id?: number;
}

const PostModification: React.FC<PostModificationProps> = ({ isEdit = false, imageUrl, editContent, editTitle, id }) => {
    const fileInputRef = useRef<HTMLInputElement>(null);
    const navigate = useNavigate();

    const { setSnackBar } = useContext(SnackBarContext);

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [headerImage, setHeaderImage] = useState<File | null>(null);
    const [headerImageUrl, setHeaderImageUrl] = useState<string | null>(null);
    const [imageSrc, setImageSrc] = useState(imageUrl);

    const [showPreview, setShowPreview] = useState<boolean | null>(null);
    const [outlined, setOutlined] = useState(true);
    const [inverted, setInverted] = useState(false);

    useEffect(() => {
        wrapPreviewButton(true)
        if (editContent) {
            setContent(editContent);
        }
        if (editTitle) {
            setTitle(editTitle);
        }
    }, []);

    const openSnack = (severity: Severity, message: string) => {
        setSnackBar({ ...defaultSnackBar, open: true, severity: severity, message: message });
    }

    const wrapPreviewButton = (firstTime = false) => {
        const isWrapped = isContentWrapped('create-post-content');

        const previewButtonContainer = document.getElementsByClassName('preview-button-container')[0];
        previewButtonContainer.classList.remove('preview-button-right', 'preview-button-below', 'no-margin-left')

        if (isWrapped) {
            previewButtonContainer.classList.add('preview-button-below');
        } else {
            previewButtonContainer.classList.add('preview-button-right');
            if (firstTime || showPreview) {
                previewButtonContainer.classList.add('no-margin-left');
            }
        }
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
            setImageSrc(undefined);
        }
    }

    const createPost = async () => {
        if (!headerImage) return

        const imageURL = imageSrc ? imageSrc : await addImage(headerImage, openSnack);
        if (!imageURL) return;

        const response = await addPost(title, content, imageURL, openSnack);

        navigate('/posts/' + response?.id);
    }

    const editPost = async () => {
        if (!id) return

        let imageURL: string | void | undefined = imageSrc
        if (!imageURL) {
            if (!headerImage) return

            imageURL = await addImage(headerImage, openSnack);
        }

        if (!imageURL) return;

        const response = await updatePost(id, title, content, imageURL, openSnack);

        navigate('/posts/' + response?.id);
    }

    const handleModification = async () => {
        if (isEdit) {
            editPost()
        } else {
            createPost()
        }
    }

    return (
        <div className='create-post-container'>
            <div className='page-header-title'>
                {
                    isEdit ? 'Редагування публікації' : 'Створення публікації'
                }
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
                {(imageSrc || headerImageUrl) &&
                    <img
                        className='post-header-image-preview'
                        src={imageSrc || headerImageUrl || ''}
                        alt="Header preview"
                    />
                }
            </div>
            <div className='create-post-section-title'>
                Заголовок:
            </div>
            <div className={`${showPreview ? 'spread ' : ''}create-post-content`}>
                <textarea
                    className='header-textarea centered post-textarea'
                    maxLength={255}
                    value={title}
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


                <Markdown
                    className={`${!showPreview ? 'invisible ' : ''}post-preview`}
                    content={content}
                />
            </div >
            <div className='create-post-button'>
                <Button inverted onClick={handleModification}>
                    {isEdit ? 'Зберегти' : 'Створити'}
                </Button>
            </div>
        </div>
    );
};

export default PostModification;