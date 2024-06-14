import React, { useContext, useState, useEffect } from 'react';
import './Comment.css';

import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';
import ReplyIcon from '@mui/icons-material/Reply';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

import Link from '../Link';
import CommentBox from '../CommentBox';
import { addComment, deleteComment, updateComment } from '../../api-calls/Comment';
import { defaultSnackBar, Severity, SnackBarContext } from '../../contexts/SnackBarContext';
import { AuthContext } from '../../contexts/AuthContext';
import { UserRole } from '../../models/User';
import { addCommentReaction, deleteCommentReaction } from '../../api-calls/CommentReaction';
import { useNavigate } from 'react-router-dom';
import { SignedComment } from '../../models/Comment';

interface CommentProps {
    comment: SignedComment;
}

const Comment: React.FC<CommentProps> = ({ comment }) => {
    const { id, role } = useContext(AuthContext);

    const [myReaction, setMyReaction] = useState(comment.myReaction);
    const [likes, setLikes] = useState(comment.likes);
    const [dislikes, setDislikes] = useState(comment.dislikes);

    const MAX_CONTENT_LENGTH = 500;
    const MAX_AUTHOR_NAME_LENGTH = 35;
    const [isEditing, setIsEditing] = useState(false);
    const [isReplying, setIsReplying] = useState(false);

    const [isModified, setIsModified] = useState(comment.isModified);
    const [isFullContentShown, setIsFullContentShown] = useState(false);
    const [commentContent, setCommentContent] = useState('');
    const [processedCommentContent, setProcessedCommentContent] = useState('');
    const [editCommentContent, setEditCommentContent] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        setProcessedCommentContent(getContent(comment.content));
        if (localStorage.getItem('scrollToBottom')) {
            window.scrollTo(0, document.body.scrollHeight);
            localStorage.removeItem('scrollToBottom');
        }
    }, []);

    const { setSnackBar } = useContext(SnackBarContext);
    const openSnack = (severity: Severity, message: string) => {
        setSnackBar({ ...defaultSnackBar, open: true, severity: severity, message: message });
    }

    const pfp = comment.authorPfpUrl ? comment.authorPfpUrl : 'https://github.com/googlefonts/noto-emoji/blob/main/png/512/emoji_u1f438.png?raw=true';

    const handleReplyOpening = () => {
        if (!comment.isDeleted) {
            setIsReplying(!isReplying);
        }
    }

    const shortenString = (str: string, length: number, ellipsisize = true, finishOnWordEnd = true): string => {
        if (str.length > length) {
            let end = length;
            if (finishOnWordEnd) {
                end = str.lastIndexOf(' ', length);
                end = end === -1 ? length : end;
            }
            return `${str.slice(0, end)}${ellipsisize ? '…' : ''}`;
        }
        return str;
    };

    const getContent = (content: string) => !content ? 'DELETED' : isFullContentShown || content.length <= MAX_CONTENT_LENGTH
        ? content
        : shortenString(content, MAX_CONTENT_LENGTH, false, true);


    const parseReactionsCount = (count: number): string => {
        const units = [
            { value: 1_000_000_000, symbol: 'B' },
            { value: 1_000_000, symbol: 'M' },
            { value: 1_000, symbol: 'K' },
        ];

        const { value, symbol } = units.find(unit => count >= unit.value) || { value: 1, symbol: '' };
        const result = (count / value).toFixed(1);
        return parseFloat(result) + symbol;
    };

    const refresh = () => {
        localStorage.setItem('scrollToBottom', 'true');
        window.location.reload();
    }

    const handleCommentAdding = async () => {
        if (role === UserRole.UNAUTHORIZED) {
            navigateToLogin();
            return
        }

        const commentResponse = await addComment(comment.postId, comment.commentId, commentContent, openSnack)

        if (!commentResponse) return;

        refresh();
    }

    const handleTextareaChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setCommentContent(event.target.value);
    }

    const handleEditTextareaChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setEditCommentContent(event.target.value);
    }

    const canModify = () => {
        return role === UserRole.ADMIN || role === UserRole.SUPER_ADMIN || id === comment.userId;
    }

    const removeTheReaction = (reaction: string) => {
        if (reaction === "LIKE") {
            setLikes(likes - 1);
        } else if (reaction === "DISLIKE") {
            setDislikes(dislikes - 1);
        }
    }

    const addTheReaction = (reaction: string) => {
        if (reaction === "LIKE") {
            setLikes(likes + 1);
        } else if (reaction === "DISLIKE") {
            setDislikes(dislikes + 1);
        }
    }

    const deleteReaction = async () => {
        const result = deleteCommentReaction(comment.commentId, openSnack);

        if (myReaction) removeTheReaction(myReaction)

        setMyReaction(null);

        return await result;
    }

    const navigateToLogin = () => {
        navigate('/login')
    }

    const addReaction = async (reaction: string) => {
        if (role === UserRole.UNAUTHORIZED) {
            navigateToLogin();
            return
        }

        const result = addCommentReaction(comment.commentId, reaction, openSnack);

        if (myReaction) removeTheReaction(myReaction)
        addTheReaction(reaction);

        setMyReaction(reaction)

        return await result;
    }

    const handleReaction = async (reaction: string) => {
        if (myReaction === reaction) {
            await deleteReaction();
        }
        if (myReaction !== reaction) {
            return addReaction(reaction);
        }
    }

    const handleCommentEditing = async () => {
        if (editCommentContent && editCommentContent !== commentContent) {
            console.log(editCommentContent, commentContent)
            var result = await updateComment(comment.commentId, editCommentContent, openSnack);

            if (result) {
                setCommentContent(result.content);
                setProcessedCommentContent(getContent(result.content));
                setIsModified(result.isModified);
            }
        }
        setIsEditing(false);
    }

    const handleDelete = async () => {
        if (!comment.isDeleted) {
            await deleteComment(comment.commentId, openSnack);
            refresh();
        }
    }

    const wrapInLinkIfNotNull = (urn: string, condition: any, children: React.ReactNode): React.ReactNode => {
        return (
            condition
                ?
                <Link to={urn}>
                    {children}
                </Link>
                :
                children
        )
    }


    const renderCommentSection = (comment: SignedComment, children: React.ReactNode): React.ReactNode => (
        <>
            <div key={comment.commentId} className='comment'>
                <div className='comment-container'>
                    {
                        wrapInLinkIfNotNull(`/users/${comment.userId}`, comment.userId, <img src={pfp} className='pfp' />)
                    }
                    <section className='top-comment-right-section'>
                        <section className='top-comment-section'>
                            <div className='comment-author-section'>
                                {
                                    wrapInLinkIfNotNull(`/users/${comment.userId}`, comment.userId, <span className='comment-author'>{shortenString(comment.authorName, MAX_AUTHOR_NAME_LENGTH)}</span>)
                                }
                                < time className='comment-date' dateTime={comment.commentedAt ? comment.commentedAt.toLocaleString() : ''}>
                                    {comment.commentedAt ? comment.commentedAt.toLocaleString() : ''}
                                </time>
                                {
                                    isModified &&
                                    <span className='edited-comment'>Edited</span>
                                }
                            </div>
                        </section>
                        {isEditing ? (
                            <CommentBox
                                value={processedCommentContent}
                                fullWidth
                                autoFocus
                                onClick={handleCommentEditing}
                                onChange={(e) => handleEditTextareaChange(e)}
                                onBlur={() => setIsEditing(false)}
                            />
                        ) : (
                            <div className='comment-content'>
                                {processedCommentContent}
                                {(comment.content ? comment.content.length > MAX_CONTENT_LENGTH : false) && (
                                    <a onClick={() => setIsFullContentShown(!isFullContentShown)}>
                                        {isFullContentShown
                                            ? <div className="wrap-text">…<br />Показати менше</div>
                                            : <div className="wrap-text">…<br />Докладніше</div>}
                                    </a>
                                )}
                            </div>
                        )}
                        <div className="comment-controls">
                            <div className="comments-reactions">
                                <section className='comment-reaction' onClick={() => { if (!comment.isDeleted) handleReaction("LIKE") }}>
                                    <ThumbUpIcon className={`${myReaction === "LIKE" ? 'active ' : ''}reactions-icon`} />
                                    <span>
                                        {parseReactionsCount(likes)}
                                    </span>
                                </section>
                                <section className='comment-reaction' onClick={() => { if (!comment.isDeleted) handleReaction("DISLIKE") }}>
                                    <ThumbDownIcon className={`${myReaction === "DISLIKE" ? 'active ' : ''}reactions-icon`} />
                                    <span>
                                        {parseReactionsCount(dislikes)}
                                    </span>
                                </section>
                                <section onClick={handleReplyOpening}>
                                    <ReplyIcon
                                        sx={{ width: '1.75rem', height: '1.75rem' }}
                                        className={`${isReplying ? 'active ' : ''}reactions-icon`}
                                    />
                                </section>
                            </div>
                            {canModify() &&
                                <div className="modification-icons">
                                    <section>
                                        <EditIcon className='reactions-icon' onClick={() => setIsEditing(true)} />
                                    </section>
                                    <section onClick={handleDelete}>
                                        <DeleteIcon className='reactions-icon' />
                                    </section>
                                </div>
                            }
                        </div>
                        {isReplying && <CommentBox autoFocus onChange={handleTextareaChange} onClick={handleCommentAdding} />}
                    </section>
                </div>
                {children}
            </div>
        </>
    );

    const renderComment = (comment: SignedComment): React.ReactNode => (
        renderCommentSection(comment,
            <>
                {comment.parentComment && renderParentComment(comment.parentComment)}
                {comment.subComments.map((childComment, index) => renderChildComment(childComment, index))}
            </>
        )
    );

    const renderParentComment = (comment: SignedComment): React.ReactNode => (
        <Comment comment={comment as SignedComment} />
    );

    const renderChildComment = (comment: SignedComment, index: number): React.ReactNode => (
        <Comment key={index} comment={comment as SignedComment} />
    );

    return <>
        {renderComment(comment)}
    </>;
};

export default Comment;