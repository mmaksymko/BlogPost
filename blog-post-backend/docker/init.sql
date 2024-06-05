CREATE DATABASE blog_post;

\c blog_post;

CREATE TABLE IF NOT EXISTS post (
    post_id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    posted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_post_user ON post (user_id);

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
CREATE INDEX IF NOT EXISTS idx_comment_user ON comment (user_id);
CREATE INDEX IF NOT EXISTS idx_comment_parent_comment ON comment (parent_comment_id);


CREATE DATABASE reaction;
\c reaction;

CREATE TABLE IF NOT EXISTS reaction_type (
	reaction_type_id SERIAL primary key NOT NULL,
	name text unique not null
);

CREATE TABLE IF NOT EXISTS post_reaction (
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
	reaction_type_id bigint not null references reaction_type(reaction_type_id),
	primary key(post_id, user_id)
);
CREATE INDEX IF NOT EXISTS idx_post_reaction_user ON post_reaction (user_id);
CREATE INDEX IF NOT EXISTS idx_post_reaction_post ON post_reaction (post_id);
CREATE INDEX IF NOT EXISTS idx_post_reaction_type ON post_reaction (reaction_type_id);

CREATE TABLE IF NOT EXISTS comment_reaction(
    comment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
	reaction_type_id int references reaction_type(reaction_type_id),
	primary key(comment_id, user_id)
);
CREATE INDEX IF NOT EXISTS idx_comment_reaction_user ON comment_reaction (user_id);
CREATE INDEX IF NOT EXISTS idx_comment_reaction_comment ON comment_reaction (comment_id);
CREATE INDEX IF NOT EXISTS idx_comment_reaction_type ON comment_reaction (reaction_type_id);

CREATE DATABASE user_db;
\c user_db;

CREATE DOMAIN USER_ROLE_DOMAIN AS TEXT
CHECK (VALUE IN ('USER', 'ADMIN', 'SUPER_ADMIN'));

CREATE TABLE IF NOT EXISTS blog_user (
    user_id SERIAL PRIMARY KEY,
	email TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    role USER_ROLE_DOMAIN NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_blog_user_email ON blog_user (email);

CREATE TABLE user_pfp (
    pfp_url TEXT NOT NULL,
    user_id INTEGER REFERENCES blog_user(user_id) ON DELETE CASCADE,
    PRIMARY KEY (pfp_url, user_id)
);

CREATE INDEX IF NOT EXISTS idx_user_pfp_user_id ON user_pfp (user_id);

