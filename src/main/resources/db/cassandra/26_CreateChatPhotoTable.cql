CREATE TABLE chat_photos_by_chat_id (
    id uuid,
    chat_id bigint,
    file_name text,
    small_file_path text,
    big_file_path text,
    created_at timestamp,
    PRIMARY KEY (chat_id, id)
)