CREATE TABLE chats_by_id (
    id bigint primary key,
    type text,
    username text,
    title text,
    description text,
    invite_link text,
    join_by_request boolean,
    has_hidden_members boolean,
    created_at timestamp,
    updated_at timestamp
);

CREATE TABLE chats_by_username (
    id bigint,
    type text,
    username text,
    title text,
    description text,
    invite_link text,
    join_by_request boolean,
    has_hidden_members boolean,
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (username, id)
);