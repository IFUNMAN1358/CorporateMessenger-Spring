DROP TABLE messenger_ks.messages_by_chat_id;

CREATE TABLE messages_by_chat_id (
    id TIMEUUID,
    chat_id bigint,
    sender_id uuid,
    sender_username text,
    content text,
    has_files boolean,
    is_changed boolean,
    is_read boolean,
    created_at timestamp,
    PRIMARY KEY (chat_id, id)
) WITH CLUSTERING ORDER BY (id DESC);