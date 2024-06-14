import React, { useContext, useEffect, useState } from 'react';
import './Post.css';

import { useNavigate, useParams } from "react-router-dom";

import { ReactedSignedPost } from '../../models/Post';
import Markdown from '../../components/Markdown';
import { defaultSnackBar, Severity, SnackBarContext } from '../../contexts/SnackBarContext';
import { deletePost, getPost } from '../../api-calls/Post';
import { getUser } from '../../api-calls/User';
import PostComponent from '../../components/Post';

import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';

import { AuthContext } from '../../contexts/AuthContext';

import Button from '../../components/Button';
import Comment from '../../components/Comment';
import CommentBox from '../../components/CommentBox';

import { UserRole } from '../../models/User';
import { Page, emptyPage } from '../../models/Page';
import { CommentResponse, SignedComment } from '../../models/Comment';

import { addComment, getComments } from '../../api-calls/Comment';
import { getCommentReactionCount, getUserCommentReaction } from '../../api-calls/CommentReaction';
import { addPostReaction, deletePostReaction, getPostReactionCount, getUserPostReaction } from '../../api-calls/PostReaction';

interface AuthorData {
    name: string;
    pfp: string;
};

interface AuthorInfo extends AuthorData {
    id: number
};

const Post: React.FC = () => {
    const { id: postId } = useParams<{ id: string }>();
    const [post, setPost] = useState<ReactedSignedPost | null>(null);
    const [commentContent, setCommentContent] = useState('');
    const [last, setLast] = useState(true);

    const [likes, setLikes] = useState(0);
    const [dislikes, setDislikes] = useState(0);
    const [myReaction, setMyReaction] = useState<string | null>(null);

    const [commentsPage, setCommentsPage] = useState<Page<CommentResponse>>(emptyPage)
    const [comments, setComments] = useState<SignedComment[]>([]);

    const { id, role } = useContext(AuthContext);

    const { setSnackBar } = useContext(SnackBarContext);
    const openSnack = (severity: Severity, message: string) => {
        setSnackBar({ ...defaultSnackBar, open: true, severity: severity, message: message });
    }

    useEffect(() => {
        fetchPost();
        fetchComments();
    }, []);

    const navigate = useNavigate();

    const navigateToLogin = () => navigate('/login')
    const navigateToHome = () => navigate('/')
    const navigateToNoPage = () => navigate('/no-page')

    const fetchAuthorNameAndPfp = (authorId: number): Promise<AuthorInfo> => {
        const onSuccess = (response: any): AuthorInfo => ({
            id: authorId,
            name: response.data.firstName + " " + response.data.lastName,
            pfp: response.data.pfpUrl
        });
        const onError = () => ({
            id: authorId,
            name: "DELETED",
            pfp: ""
        })

        return getUser(authorId, onSuccess, onError);
    }

    const getMyCommentReaction = async (commentId: number) => (await getUserCommentReaction(commentId, openSnack)).reaction?.name
    const getCommentReactions = async (commentId: number) => await getCommentReactionCount(commentId, openSnack);

    const getMyPostReaction = async (commentId: number) => (await getUserPostReaction(commentId, openSnack)).reaction?.name
    const getPostReactions = async (commentId: number) => await getPostReactionCount(commentId, openSnack);

    const getDate = (date: Date) => date ? new Date(`${date}Z`) : null;

    const mapToSignedBaseComment = async (comment: CommentResponse, authorsMap: Record<number, AuthorData>): Promise<SignedComment> => {
        const reactions = await getCommentReactions(comment.commentId);
        const myReaction = await getMyCommentReaction(comment.commentId);
        return {
            ...comment,
            commentedAt: getDate(comment.commentedAt),
            parentComment: null,
            subComments: [],
            authorName: authorsMap[comment.userId].name,
            authorPfpUrl: authorsMap[comment.userId].pfp,
            likes: reactions.LIKE,
            dislikes: reactions.DISLIKE,
            myReaction: myReaction
        };
    };

    const mapToSignedChildlessComment = async (comment: CommentResponse, authorsMap: Record<number, AuthorData>): Promise<SignedComment> => {
        const baseComment = await mapToSignedBaseComment(comment, authorsMap);
        return {
            ...baseComment,
            commentedAt: getDate(comment.commentedAt),
            parentComment: comment.parentComment ? await mapToSignedChildlessComment(comment.parentComment, authorsMap) : null
        };
    };

    const mapToSignedParentlessComment = async (comment: CommentResponse, authorsMap: Record<number, AuthorData>): Promise<SignedComment> => {
        const baseComment = await mapToSignedBaseComment(comment, authorsMap);
        return {
            ...baseComment,
            subComments: await Promise.all(comment.subComments.map(subComment => mapToSignedParentlessComment(subComment, authorsMap)))
        };
    };

    const mapToSignedComment = async (comment: CommentResponse, authorsMap: Record<number, AuthorData>): Promise<SignedComment> => {
        const baseComment = await mapToSignedBaseComment(comment, authorsMap);
        return {
            ...baseComment,
            commentedAt: getDate(comment.commentedAt),
            parentComment: comment.parentComment ? await mapToSignedChildlessComment(comment.parentComment, authorsMap) : null,
            subComments: await Promise.all(comment.subComments.map(subComment => mapToSignedParentlessComment(subComment, authorsMap)))
        };
    };

    const extractUserIds = (comment: CommentResponse): number[] => {
        let ids = [comment.userId];

        if (comment.subComments) {
            for (let subComment of comment.subComments) {
                ids = [...ids, ...extractUserIds(subComment as CommentResponse)];
            }
        }

        if (comment.parentComment) {
            ids = [...ids, ...extractUserIds(comment.parentComment as CommentResponse)];
        }

        return ids;
    }

    const fetchComments = async () => {
        if (!postId || !post?.title) return;

        const page = await getComments(postId, commentsPage.pageable.pageNumber, openSnack)
        if (!page) return;

        setLast(page.last);
        page.pageable.pageNumber++
        setCommentsPage(page);

        const comments = page.content

        const userIds = comments.flatMap(extractUserIds)
            .filter((value, index, self) => self.indexOf(value) === index);

        const authors = await Promise.all(userIds.map(async userId => await fetchAuthorNameAndPfp(userId)));

        const authorsMap: Record<number, AuthorData> = authors.reduce((map, author) => {
            map[author.id] = { name: author.name, pfp: author.pfp };
            return map;
        }, {} as Record<number, AuthorData>);

        const signedComments = await Promise.all(comments.map(comment => mapToSignedComment(comment, authorsMap)));

        setComments(prevComments => [...prevComments, ...signedComments]);
    }

    const fetchPost = async () => {
        if (!postId) {
            navigateToNoPage()
            return;
        }

        const post = await getPost(postId)
        if (!post) {
            navigateToNoPage()
            return;
        }

        const onSuccess = (response: any) => response.data.firstName + " " + response.data.lastName
        const onError = (error: any) => setSnackBar({ ...defaultSnackBar, open: true, severity: "error", message: `Failed to fetch author name! ${error.response.data.error}` });
        const authorName: string = await getUser(post.authorId, onSuccess, onError);
        const reactions = await getPostReactions(post.id);
        const myReaction = await getMyPostReaction(post.id);

        setLikes(reactions.LIKE);
        setDislikes(reactions.DISLIKE);
        setMyReaction(myReaction);

        setPost({
            ...post,
            postedAt: post.postedAt,
            likes: reactions.LIKE,
            dislikes: reactions.DISLIKE,
            myReaction: myReaction,
            authorName: authorName
        });
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


    const addReaction = async (reaction: string) => {
        if (role === UserRole.UNAUTHORIZED || !post?.id) {
            navigateToLogin();
            return
        }

        const result = addPostReaction(post.id, reaction, openSnack);

        if (myReaction) removeTheReaction(myReaction)
        addTheReaction(reaction);

        setMyReaction(reaction)

        return await result;
    }

    const deleteReaction = async () => {
        if (!post) return;

        const result = deletePostReaction(post.id, openSnack);

        if (myReaction) removeTheReaction(myReaction)

        setMyReaction(null);

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

    const handleCommentAdding = async () => {
        if (!postId || !commentContent) return;
        if (role === UserRole.UNAUTHORIZED) {
            navigateToLogin();
            return;
        }
        const comment = await addComment(parseInt(postId), null, commentContent, openSnack)

        if (!comment) return;

        const userIds = [comment].flatMap(extractUserIds)
            .filter((value, index, self) => self.indexOf(value) === index);

        const authors = await Promise.all(userIds.map(async userId => await fetchAuthorNameAndPfp(userId)));

        const authorsMap: Record<number, AuthorData> = authors.reduce((map, author) => {
            map[author.id] = { name: author.name, pfp: author.pfp };
            return map;
        }, {} as Record<number, AuthorData>);

        const signedComments = await Promise.all([comment].map(comm => mapToSignedComment(comm, authorsMap)));
        setComments(prevComments => [...signedComments, ...prevComments]);

        commentsPage.totalElements++
    }

    const handleTextareaChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => setCommentContent(event.target.value);

    const isSignedComment = (comment: any): comment is SignedComment => comment.parentComment !== undefined;

    const countComments = (comment: SignedComment): number => {
        return isSignedComment(comment)
            ? 1 + comment.subComments.reduce((total, subComment) => total + countComments(subComment), 0)
            : 1
    }
    const totalComments = comments.reduce((total, comment) => total + countComments(comment), 0);

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

    const handleDelete = async () => {
        if (!post) return;

        await deletePost(post.id, openSnack);
        navigateToHome();
    }

    return (
        <div className="post">
            {post &&
                <div className='post-header'>
                    <PostComponent clickable={false} post={post} />
                </div>
            }
            <section className="post-under-header-section">
                <div className="reaction-icons">
                    <div className='post-reaction'>
                        <Button
                            inverted={myReaction !== "LIKE"}
                            outlined={myReaction === "LIKE"}
                            width='3rem'
                            height='3rem'
                            onClick={() => handleReaction("LIKE")}
                        >
                            <ThumbUpIcon />
                        </Button>
                        <span className='post-reaction-count'>{parseReactionsCount(likes)}</span>
                    </div>
                    <div className='post-reaction'>
                        <Button
                            inverted={myReaction !== "DISLIKE"}
                            outlined={myReaction === "DISLIKE"}
                            width='3rem'
                            height='3rem'
                            onClick={() => handleReaction("DISLIKE")}
                        >
                            <ThumbDownIcon />
                        </Button>
                        <span className='post-reaction-count'>{parseReactionsCount(dislikes)}</span>
                    </div>
                </div>
                {(id == post?.authorId || (role === UserRole.SUPER_ADMIN || role === UserRole.ADMIN)) &&
                    <div className="modify-icons">
                        <Button inverted width='3rem' height='3rem'>
                            <EditIcon />
                        </Button>
                        <Button inverted width='3rem' height='3rem' onClick={handleDelete}>
                            <DeleteIcon />
                        </Button>
                    </div>
                }
            </section>
            <Markdown className="post-content" content={post?.content} />
            <div className='post-comments'>
                <div className='page-header-title font-size-1-5rem'>
                    Коментарі ({totalComments})
                </div>
                <div className='comment-box-container'>
                    <CommentBox minRows={2} maxRows={6} placeholder='Напишіть коментар...' onChange={handleTextareaChange} onClick={handleCommentAdding} />
                </div>
                <div className='comments-container'>
                    {comments.map(comment => <Comment comment={comment} key={comment.commentId} />)}
                </div>
                {!last && <div className="load-more-posts" onClick={fetchComments}>Load more...</div>}
            </div>
        </div>
    );
};

export default Post;