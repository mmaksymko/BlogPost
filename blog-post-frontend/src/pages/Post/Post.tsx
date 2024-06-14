import React, { useContext, useEffect, useState } from 'react';
import './Post.css';

import { useParams } from "react-router-dom";

import { SignedPost } from '../../models/Post';
import Markdown from '../../components/Markdown';
import { defaultSnackBar, Severity, SnackBarContext } from '../../contexts/SnackBarContext';
import { getPost } from '../../api-calls/Post';
import { getUser } from '../../api-calls/User';
import PostComponent from '../../components/Post';

import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';

import Button from '../../components/Button';
import Comment from '../../components/Comment';
import { AuthContext } from '../../contexts/AuthContext';
import { UserRole } from '../../models/User';
import { Page, emptyPage } from '../../models/Page';
import { addComment, getComments } from '../../api-calls/Comment';
import CommentBox from '../../components/CommentBox';
import { getCommentReactionCount, getUserReaction } from '../../api-calls/CommentReaction';
import { CommentResponse, SignedComment } from '../../models/Comment';

interface AuthorData {
    name: string;
    pfp: string;
};

interface AuthorInfo extends AuthorData {
    id: number
};

const Post: React.FC = () => {
    const { id: postId } = useParams<{ id: string }>();
    const [post, setPost] = useState<SignedPost | null>(null);
    const [commentContent, setCommentContent] = useState('');
    const [last, setLast] = useState(false);


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

    const getMyReaction = async (commentId: number) => {
        const reaction = (await getUserReaction(commentId, openSnack)).reaction
        return reaction?.name
    }

    const getReactions = async (commentId: number) => {
        return await getCommentReactionCount(commentId, openSnack);
    }

    const getDate = (date: Date) => date ? new Date(`${date}Z`) : null;

    const mapToSignedBaseComment = async (comment: CommentResponse, authorsMap: Record<number, AuthorData>): Promise<SignedComment> => {
        const reactions = await getReactions(comment.commentId);
        const myReaction = await getMyReaction(comment.commentId);
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
        if (!postId) return;

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
        if (!postId) return;

        const post = await getPost(postId, openSnack)
        if (!post) return;

        const onSuccess = (response: any) => response.data.firstName + " " + response.data.lastName
        const onError = (error: any) => setSnackBar({ ...defaultSnackBar, open: true, severity: "error", message: `Failed to fetch author name! ${error.response.data.error}` });
        const authorName: string = await getUser(post.authorId, onSuccess, onError);

        setPost({
            ...post,
            authorName
        });
    }

    const handleCommentAdding = async () => {
        if (!postId || !commentContent) return;

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

    const handleTextareaChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setCommentContent(event.target.value);
    }

    const isSignedComment = (comment: any): comment is SignedComment => {
        return comment.parentComment !== undefined;
    }

    const countComments = (comment: SignedComment): number => {
        return isSignedComment(comment)
            ? 1 + comment.subComments.reduce((total, subComment) => total + countComments(subComment), 0)
            : 1
    }
    const totalComments = comments.reduce((total, comment) => total + countComments(comment), 0);

    return (
        <div className="post">
            {post &&
                <div className='post-header'>
                    <PostComponent clickable={false} post={post} />
                </div>
            }
            <section className="post-under-header-section">
                <div className="reaction-icons">
                    <Button inverted width='3rem' height='3rem'>
                        <ThumbUpIcon />
                    </Button>
                    <Button inverted width='3rem' height='3rem'>
                        <ThumbDownIcon />
                    </Button>
                </div>
                {(id == post?.authorId || (role === UserRole.SUPER_ADMIN || role === UserRole.ADMIN)) &&
                    <div className="modify-icons">
                        <Button inverted width='3rem' height='3rem'>
                            <EditIcon />
                        </Button>
                        <Button inverted width='3rem' height='3rem'>
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