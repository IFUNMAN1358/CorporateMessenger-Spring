CREATE TABLE group_chat_photos_by_chat_id (
    id timeuuid,
    chat_id timeuuid,
    file_name text,
    file_path text,
    content_type text,
    created_at timestamp,
    PRIMARY KEY (chat_id, id)
) WITH CLUSTERING ORDER BY (id DESC);