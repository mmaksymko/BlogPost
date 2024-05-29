CREATE DATABASE blog_post;

\c blog_post;

CREATE TABLE IF NOT EXISTS post (
    post_id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    posted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE DATABASE blog_post_comment;

\c blog_post_comment;

CREATE TABLE IF NOT EXISTS comment (
    comment_id SERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    parent_comment_id BIGINT REFERENCES comment(comment_id),
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    is_modified BOOLEAN NOT NULL DEFAULT FALSE,
    commented_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);