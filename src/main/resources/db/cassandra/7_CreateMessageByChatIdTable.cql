CREATE TABLE messages_by_chat_id (
    id TIMEUUID,
    chat_id uuid,
    sender_id uuid,
    content text,
    has_files boolean,
    is_changed boolean,
    is_read boolean,
    created_at timestamp,
    PRIMARY KEY (chat_id, id)
) WITH CLUSTERING ORDER BY (id DESC);