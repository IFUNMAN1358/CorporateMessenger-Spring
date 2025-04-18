CREATE TABLE chat_members_by_user_id (
    chat_id bigint,
    user_id uuid,
    status text,
    joined_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (user_id, chat_id)
);

CREATE TABLE chat_members_by_chat_id (
    chat_id bigint,
    user_id uuid,
    status text,
    joined_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (chat_id, user_id)
);