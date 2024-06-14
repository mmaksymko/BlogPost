import React, { useContext, useEffect, useState } from 'react';
import './EditPost.css';

import { useNavigate, useParams } from "react-router-dom";

import { PostResponse } from '../../models/Post';
import { defaultSnackBar, Severity, SnackBarContext } from '../../contexts/SnackBarContext';
import { getPost } from '../../api-calls/Post';

import { AuthContext } from '../../contexts/AuthContext';
import { UserRole } from '../../models/User';

import PostModification from '../../components/PostModification';

const EditPost: React.FC = () => {
    const { id: postId } = useParams<{ id: string }>();
    const [post, setPost] = useState<PostResponse | null>(null);
    const { id: userId, role } = useContext(AuthContext);

    const navigate = useNavigate();

    const { setSnackBar } = useContext(SnackBarContext);
    const openSnack = (severity: Severity, message: string) => {
        setSnackBar({ ...defaultSnackBar, open: true, severity: severity, message: message });
    }

    useEffect(() => {
        fetchPost();
    }, []);

    const canModify = (user: number) => {
        console.log(user)
        console.log(userId)
        return role === UserRole.ADMIN || role === UserRole.SUPER_ADMIN || userId === user;
    }

    const fetchPost = async () => {
        if (!postId) return;

        const fetchedPost = await getPost(postId, openSnack)

        if (!fetchedPost || !canModify(fetchedPost.authorId)) {
            navigate('/');
        }

        setPost(fetchedPost);
    }

    return (
        post ? (
            <PostModification
                isEdit={true}
                imageUrl={post.headerImageURL}
                editContent={post.content}
                editTitle={post.title}
                id={post.id}
            />
        ) : null
    );
}

export default EditPost;