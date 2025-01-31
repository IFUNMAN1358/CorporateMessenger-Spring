CREATE TABLE private_chats_by_first_user (
    id UUID,
    first_user_id UUID,
    second_user_id UUID,
    last_message_id UUID,
    created_at TIMESTAMP,
    is_available BOOLEAN,
    PRIMARY KEY (first_user_id, id)
);