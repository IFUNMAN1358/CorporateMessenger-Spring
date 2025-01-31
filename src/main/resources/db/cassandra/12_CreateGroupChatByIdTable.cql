CREATE TABLE group_chats_by_id (
    id uuid primary key,
    name text,
    description text,
    owner_id uuid,
    file_path text,
    last_message_id uuid,
    is_public boolean,
    updated_at timestamp,
    created_at timestamp
);