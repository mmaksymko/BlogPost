import React, { useContext, useEffect, useState } from 'react';
import './Post.css';

import { useParams } from "react-router-dom";

import { SignedPost } from '../../models/Post';
import Markdown from '../../components/Markdown';
import { defaultSnackBar, Severity, SnackBarContext } from '../../contexts/SnackBarContext';
import { getPost } from '../../api-calls/Post';
import { getUser } from '../../api-calls/User';
import PostComponent from '../../components/Post';

import Divider from '@mui/material/Divider';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';

import Button from '../../components/Button';
import { AuthContext } from '../../contexts/AuthContext';
import { UserRole } from '../../models/User';
import { SignedComment } from '../../models/Comment';


const Post: React.FC = () => {
    const { id: postId } = useParams<{ id: string }>();
    const [post, setPost] = useState<SignedPost | null>(null);
    const [comments, setComments] = useState<SignedComment[] | null>(null);

    const { id, role } = useContext(AuthContext);

    const { setSnackBar } = useContext(SnackBarContext);
    const openSnack = (severity: Severity, message: string) => {
        setSnackBar({ ...defaultSnackBar, open: true, severity: severity, message: message });
    }

    useEffect(() => {
        fetchPost();
    }, []);

    const fetchPost = async () => {
        if (!postId) return;

        const post = await getPost(postId, openSnack)
        if (!post) return;

        const onSuccess = (response: any) => response.data.firstName + " " + response.data.lastName
        const onError = (error: any) => setSnackBar({ ...defaultSnackBar, open: true, severity: "error", message: `Failed to fetch author name! ${error.response.data.error}` });
        const authorName = await getUser(post.authorId, onSuccess, onError);

        setPost({
            ...post,
            authorName
        });
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
                <div className='page-header-title'>
                    Коментарі ({ })
                </div>
            </div>
        </div>
    );
};

export default Post;