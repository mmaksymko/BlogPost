import React from 'react';

import './Posts.css';
import { SignedPost } from '../../models/Post';
import Post from '../Post'
import Link from '../Link'

interface PostsProps {
    posts: SignedPost[]
}

const Posts: React.FC<PostsProps> = ({ posts }) => {
    return (
        <div className="blog-posts-container">
            {
                posts.map(post => <Link to={`posts/${post.id}`}><Post post={post} key={`post ${post.id}`} /></Link>)
            }
        </div >
    );
};

export default Posts;