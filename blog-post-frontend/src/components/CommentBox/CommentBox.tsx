import React, { useEffect, useRef } from 'react';
import './CommentBox.css';

import SendIcon from '@mui/icons-material/Send';

interface CommentBoxProps {
    minRows?: number;
    maxRows?: number;
    placeholder?: string;
    autoFocus?: boolean;
    onClick?: () => void;
    onChange?: (event: React.ChangeEvent<HTMLTextAreaElement>) => void;
}

const CommentBox: React.FC<CommentBoxProps> = ({ minRows = 1, maxRows = 4, placeholder, onClick, onChange, autoFocus = false }) => {
    const textareaRef = useRef<HTMLTextAreaElement>(null);

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
        }

        return () => {
            if (textareaRef.current) {
                textareaRef.current.removeEventListener('input', autoResize, false);
            }
        }
    }, [maxRows]);

    return (
        <section className="commentbox-container">
            <textarea
                ref={textareaRef}
                className="commentbox-textarea"
                placeholder={placeholder}
                rows={minRows}
                onChange={onChange}
                autoFocus={autoFocus}
            />
            <div onClick={onClick}>
                <SendIcon className='send-icon' />
            </div>
        </section>
    );
}

export default CommentBox;