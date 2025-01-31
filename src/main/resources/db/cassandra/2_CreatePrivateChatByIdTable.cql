CREATE TABLE private_chats_by_id (
    id UUID PRIMARY KEY,
    first_user_id UUID,
    second_user_id UUID,
    last_message_id UUID,
    created_at TIMESTAMP,
    is_available BOOLEAN
);