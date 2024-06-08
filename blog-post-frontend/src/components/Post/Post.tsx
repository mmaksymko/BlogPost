import React from 'react';

import './Post.css';
import { SignedPost } from '../../models/Post';
import Link from '../Link'

interface PostProps {
    post: SignedPost
}

const Post: React.FC<PostProps> = ({ post }) => {
    return (
        <div className="blog-post-container">
            <div className="blog-post-header">
                <img className="blog-post-header-image" src={post.headerImageURL}></img>
                <span className="blog-post-title">{post.title}</span>
            </div>
            <div className="blog-post-description">
                <Link to={`authors/${post.authorId}`}>
                    <span className="blog-post-author">{post.authorName}</span>
                </Link>
                <span className="blog-post-date">{post.postedAt.toUTCString()}</span>
            </div>
        </div>
    );
};

export default Post;