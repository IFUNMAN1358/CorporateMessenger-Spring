CREATE TABLE read_messages_by_id (
    id uuid PRIMARY KEY,
    user_id uuid,
    chat_id uuid,
    message_id uuid
);