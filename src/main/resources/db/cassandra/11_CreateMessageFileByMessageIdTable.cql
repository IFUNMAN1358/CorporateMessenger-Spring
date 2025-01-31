CREATE TABLE message_files_by_message_id (
    id timeuuid,
    message_id timeuuid,
    file_name text,
    file_path text,
    content_type text,
    created_at timestamp,
    PRIMARY KEY (message_id, id)
) WITH CLUSTERING ORDER BY (id DESC);