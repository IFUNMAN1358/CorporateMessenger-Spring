CREATE TABLE read_messages_by_message_id (
    id uuid,
    user_id uuid,
    chat_id uuid,
    message_id uuid,
    PRIMARY KEY (message_id, id)
);