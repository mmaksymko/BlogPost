import React, { useContext, useState, useEffect } from 'react';
import './Comment.css';
import { SignedBaseCommentResponse, SignedChildlessCommentResponse, SignedComment, SignedParentlessCommentResponse } from '../../models/Comment';

import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';
import ReplyIcon from '@mui/icons-material/Reply';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

import Link from '../Link';
import CommentBox from '../CommentBox';
import { addComment, deleteComment } from '../../api-calls/Comment';
import { defaultSnackBar, Severity, SnackBarContext } from '../../contexts/SnackBarContext';
import { AuthContext } from '../../contexts/AuthContext';
import { UserRole } from '../../models/User';
import { addCommentReaction, deleteCommentReaction } from '../../api-calls/CommentReaction';

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

    const [isReplying, setIsReplying] = useState(false);
    const [isFullContentShown, setIsFullContentShown] = useState(false);
    const [commentContent, setCommentContent] = useState('');

    useEffect(() => {
        if (localStorage.getItem('scrollToBottom')) {
            window.scrollTo(0, document.body.scrollHeight);
            localStorage.removeItem('scrollToBottom');
        }
    }, []);

    const { setSnackBar } = useContext(SnackBarContext);
    const openSnack = (severity: Severity, message: string) => {
        setSnackBar({ ...defaultSnackBar, open: true, severity: severity, message: message });
    }

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

    const content = !comment.content || isFullContentShown || comment.content.length <= MAX_CONTENT_LENGTH
        ? comment.content
        : shortenString(comment.content, MAX_CONTENT_LENGTH, false, true);


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
        const commentResponse = await addComment(comment.postId, comment.commentId, commentContent, openSnack)

        if (!commentResponse) return;

        refresh();
    }

    const handleTextareaChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setCommentContent(event.target.value);
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

    const addReaction = async (reaction: string) => {
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

    const handleDelete = async () => {
        if (!comment.isDeleted) {
            await deleteComment(comment.commentId, openSnack);
            refresh();
        }
    }

    const renderCommentSection = (comment: SignedBaseCommentResponse, children: React.ReactNode): React.ReactNode => (
        <>
            <div key={comment.commentId} className='comment'>
                <div className='comment-container'>
                    <Link to={`/users/${comment.userId}`}>
                        <img src={comment.authorPfpUrl} className='pfp' />
                    </Link>
                    <section className='top-comment-right-section'>
                        <section className='top-comment-section'>
                            <div className='comment-author-section'>
                                <Link to={`/users/${comment.userId}`}>
                                    <span className='comment-author'>{shortenString(comment.authorName, MAX_AUTHOR_NAME_LENGTH)}</span>
                                </Link>
                                <time className='comment-date' dateTime={comment.commentedAt ? comment.commentedAt.toLocaleString() : ''}>
                                    {comment.commentedAt ? comment.commentedAt.toLocaleString() : ''}
                                </time>                            </div>
                        </section>
                        <div className='comment-content'>
                            {content}
                            {comment.content ? comment.content.length > MAX_CONTENT_LENGTH : true && (
                                <a onClick={() => setIsFullContentShown(!isFullContentShown)}>
                                    {isFullContentShown
                                        ? <div className="wrap-text">…<br />Показати менше</div>
                                        : <div className="wrap-text">…<br />Докладніше</div>}
                                </a>
                            )}
                        </div>
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
                                        <EditIcon className='reactions-icon' />
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

    const renderParentComment = (comment: SignedChildlessCommentResponse): React.ReactNode => (
        <Comment comment={comment as SignedComment} />
    );

    const renderChildComment = (comment: SignedParentlessCommentResponse, index: number): React.ReactNode => (
        <Comment key={index} comment={comment as SignedComment} />
    );

    return <>
        {renderComment(comment)}
    </>;
};

export default Comment;