CREATE TABLE private_chats_by_second_user (
    id UUID,
    first_user_id UUID,
    second_user_id UUID,
    last_message_id UUID,
    created_at TIMESTAMP,
    is_available BOOLEAN,
    PRIMARY KEY (second_user_id, id)
);