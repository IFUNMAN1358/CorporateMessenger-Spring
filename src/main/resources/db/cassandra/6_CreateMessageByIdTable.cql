CREATE TABLE messages_by_id (
    id TIMEUUID PRIMARY KEY,
    chat_id uuid,
    sender_id uuid,
    content text,
    has_files boolean,
    is_changed boolean,
    is_read boolean,
    created_at timestamp
);