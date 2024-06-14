import React, { useEffect, useRef, useState } from 'react';
import './CommentBox.css';

import SendIcon from '@mui/icons-material/Send';

interface CommentBoxProps {
    minRows?: number;
    maxRows?: number;
    placeholder?: string;
    autoFocus?: boolean;
    onClick?: () => void;
    onChange?: (event: React.ChangeEvent<HTMLTextAreaElement>) => void;
    onBlur?: () => void;
    value?: string;
    fullWidth?: boolean;
}

const CommentBox: React.FC<CommentBoxProps> = ({ minRows = 1, maxRows = 4, placeholder, onClick, onChange, onBlur, autoFocus = false, value = '', fullWidth = false }) => {
    const textareaRef = useRef<HTMLTextAreaElement>(null);
    const [text, setText] = useState(value);

    useEffect(() => {
        const autoResize = () => {
            if (textareaRef.current) {
                textareaRef.current.style.height = 'auto';
                textareaRef.current.style.height = `${Math.min(textareaRef.current.scrollHeight, maxRows * 18)}px`;
                textareaRef.current.style.overflowY = textareaRef.current.scrollHeight > maxRows * 18 ? 'scroll' : 'hidden';
            }
        }

        if (textareaRef.current) {
            textareaRef.current.addEventListener('input', autoResize, false);
            if (autoFocus) {
                const length = textareaRef.current.value.length;
                textareaRef.current.setSelectionRange(length, length);
            }
        }

        return () => {
            if (textareaRef.current) {
                textareaRef.current.removeEventListener('input', autoResize, false);
            }
        }
    }, [maxRows]);

    const handleChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setText(event.target.value);
        if (onChange) {
            onChange(event);
        }
    }

    const handleClick = (event: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
        event.preventDefault();
        event.stopPropagation();
        if (onClick) {
            onClick();
        }
    }


    return (
        <section className={`${fullWidth ? 'edit-comment ' : ''}commentbox-container`}>
            <textarea
                ref={textareaRef}
                className="commentbox-textarea"
                placeholder={placeholder}
                rows={minRows}
                onChange={handleChange}
                onBlur={onBlur}
                autoFocus={autoFocus}
                value={text}
            />
            <div onMouseDown={handleClick}>
                <SendIcon className='send-icon' />
            </div>
        </section>
    );
}

export default CommentBox;