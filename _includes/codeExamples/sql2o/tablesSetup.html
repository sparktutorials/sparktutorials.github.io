CREATE USER blog_owner WITH PASSWORD 'sparkforthewin';
CREATE DATABASE blog;
\connect blog
GRANT ALL PRIVILEGES ON DATABASE blog TO blog_owner;

CREATE TABLE posts (
    post_uuid uuid primary key,
    title text not null,
    content text,
    publishing_date date
);

CREATE TABLE comments (
    comment_uuid uuid primary key,
    post_uuid uuid references posts(post_uuid),
    author text,
    content text,
    approved bool,
    submission_date date
);

CREATE TABLE posts_categories (
    post_uuid uuid references posts(post_uuid),
    category text
);

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO blog_owner;